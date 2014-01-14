package net.mortalsilence.indiepim.server.google.authorization;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import net.mortalsilence.indiepim.server.SystemConfigKey;
import net.mortalsilence.indiepim.server.UserConfigKey;
import net.mortalsilence.indiepim.server.dao.ConfigDAO;
import net.mortalsilence.indiepim.server.google.GoogleConstants;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings("serial")
@Named
public class GoogleAuthServlet extends HttpServlet {

	private AuthorizationCodeFlow flow;
	private final Lock lock = new ReentrantLock();
	final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");
    @PersistenceContext
    private EntityManager em;
    @Inject
    private ConfigDAO configDAO;


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		EntityTransaction t = em.getTransaction();
		try {
			if(logger.isInfoEnabled())
				logger.info("Thread " + Thread.currentThread().getId() + ": Beginning transaction. ");
			t.begin();  			

			StringBuffer buf = req.getRequestURL();
			if (req.getQueryString() != null) {
				buf.append('?').append(req.getQueryString());
			}
			final AuthorizationCodeResponseUrl responseUrl = new AuthorizationCodeResponseUrl(buf.toString());
			final String code = responseUrl.getCode();
			if (responseUrl.getError() != null) {
				resp.getOutputStream().println("No valid Authentication code received. Error:");
				resp.getOutputStream().println(responseUrl.getError());
			} else if (code == null) {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				resp.getWriter().print("Missing authorization code");
			} else {
				final String redirectUri = getRedirectUri();
				lock.lock();
				try {
					final String userId = req.getParameter("state");
					if(userId == null || "".equals(userId)) {
						resp.getOutputStream().println("No valid User id received.");
						super.doGet(req, resp);
					} else {
						if (flow == null) {
							flow = initializeFlow(userId);
						}
						TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
						
						flow.createAndStoreCredential(response, userId);
						resp.getOutputStream().println("<h2 style='color:grey;font-family:Arial'>Authenticating your Google account was successful.</h2><p style='font-family:Arial'>You can close this window.<p>");					
					}
				} catch (Exception e) {
					e.printStackTrace();
					resp.getOutputStream().println("Receiving credential failed: " + e.getMessage());	
				} finally {
					lock.unlock();
				}
				
				if(logger.isInfoEnabled())
					logger.info("Thread " + Thread.currentThread().getId() + ": Commiting transaction. ");
				t.commit();
			}
		} catch(Exception e) {
			if(logger.isInfoEnabled())
				logger.info("Thread " + Thread.currentThread().getId() + ": Rolling back transaction");
			if (t.isActive()) 
				t.rollback();
			throw new RuntimeException("Persistence problem.", e);
		} finally {
			em.close();
		}				

	}

	private String getRedirectUri() {
		String baseUrl = configDAO.getSystemPropertyValue(SystemConfigKey.SERVER_BASE_URL);
		if(baseUrl == null)
			throw new RuntimeException("No valid server base url configured.");
		final String redirectUri = baseUrl + "/oauth2callback";
		return redirectUri;
	}	

	protected AuthorizationCodeFlow initializeFlow(final String userId) throws IOException {
		final String clientId = configDAO.getUserPropertyValue(new Long(userId), UserConfigKey.GOOGLE_AUTH_CLIENT_ID);
		final String clientSecret = configDAO.getUserPropertyValue(new Long(userId), UserConfigKey.GOOGLE_AUTH_CLIENT_SECRET);

		return new GoogleAuthorizationCodeFlow.Builder(new NetHttpTransport(), 
				new JacksonFactory(),
				clientId, 
				clientSecret,
				(Iterable<String>)Collections.singleton(GoogleConstants.GOOGLE_API_SCOPE_CONTACTS_V3)
				)
		.setCredentialStore(new UserConfigCredentialStore())
		.build();
	}	

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


		super.doPost(req, resp);		
	}



}
