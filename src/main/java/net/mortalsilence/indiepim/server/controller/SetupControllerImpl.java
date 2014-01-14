package net.mortalsilence.indiepim.server.controller;

import net.mortalsilence.indiepim.server.SystemConfigKey;
import net.mortalsilence.indiepim.server.dao.ConfigDAO;
import net.mortalsilence.indiepim.server.security.EncryptionService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: amievil
 * Date: 10.09.13
 * Time: 19:54
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class SetupControllerImpl implements SetupController {

    @Inject
    private EncryptionService encryptionService;
    @Inject
    private ConfigDAO configDAO;

    @Transactional
    @RequestMapping(value="/setup")
    @ResponseBody
    public String get(@RequestParam(value = "setup", required = false)final String action,
                      @RequestParam(value = "keystorepath", required = false) final String keystorePath,
                      @RequestParam(value = "url", required = false) final String url,
                      @RequestHeader(value = "Host") final String host
    ) throws IOException {

        String result = "";

        final String setupDone = configDAO.getSystemPropertyValue(SystemConfigKey.SETUP_DONE);

        if("1".equals(setupDone)) {

            result ="<body><h2>System already set up </h2>" +
                    "<a href=\"IndiePIM.html\">Continue to IndiePIM...</a></body>";

        } else if(action == null || "".equals(action)) {

            result = getSetupForms(keystorePath, host);

        }  else if ("serverBaseUrl".equals(action)) {
            // TODO check for validity
            if( url == null || "".equals(url)) {
                result += "You did not provide a valid server base URL path.";
            } else {
                try {
                    configDAO.setSystemProperty(SystemConfigKey.SERVER_BASE_URL, url);
                    result += "Server base URL set to " + url;
                    result += " <br><br><a href=\"setup\">Back to setup</a> ";
                    result += " <br><br><a href=\"IndiePIM.html\">Continue to IndiePIM...</a> ";
                } catch (Exception e) {
                    result += e.getMessage();
                }
            }
            result = "<body><h2>" + result + "</h2></body>";

        } else if ("finish".equals(action)) {
            if(configDAO.getSystemProperty(SystemConfigKey.SERVER_BASE_URL) == null) {
                result += "Server base url not set. <a href=\"setup\">Return to setup</a>";
            } else {
                configDAO.setSystemProperty(SystemConfigKey.SETUP_DONE, "1");
                // TODO implement me!
                result += "System setup marked as done. You can later modify system settings as administration user.";
                result += "<br><br><a href=\"/\">Continue to IndiePIM...</a>";
            }
            result = "<body><h2>" + result + "</h2></body>";
        }

        return result ;

    }

    private String getSetupForms(final String keystorePath, String host) {

        StringBuffer str = new StringBuffer("<html>");
        str.append("<body>");
        str.append("<h2>IndiePIM setup</h2>");
        str.append("<h3>Security</h3>");
        str.append("To encrypt your users data (e.g. mail account passwords), it is strongly recommended to get a ");
        str.append("keystore created.<br>");
        str.append("A secret key to encrypt and decrypt the data will be generated and stored in the keystore<br>");
        str.append("Make sure the keystore path is writable by your Server, but is not available to public!");
        str.append("<br><br>If you do not provide this information, your database connection information and the user message account ");
        str.append("passwords will not be encrypted! You don't want to do that. I know it sucks. ");
        str.append("But you are just about to escape Google. So why take the risk? ");
        str.append("<form action=\"setup\">");
        str.append("<b>Keystore directory path (current value: " + (keystorePath != null ? keystorePath : "not yet set") + "), without filename:</b>");
        final String path = keystorePath != null ? keystorePath : System.getProperty("user.home");
        str.append("<br><input name=\"keystorepath\"  size=\"70\" value=\"" + path + "\" />");
        str.append("<input type=\"hidden\" name=\"setup\" value=\"security\">");
        str.append("<br><br><input type=\"submit\" value=\"Set up Encryption\">");
        str.append("</form>");

        str.append("<h3>Server Base URL</h3>");
        str.append("Please configure your servers URL as seen from external clients.<br>");
        str.append("It is used for Google API request calls (redirect URL) for example.<br>");
        str.append("<b>Example:</b> <pre>http://www.mortalsilence.net</pre><br>");
        str.append("<form action=\"setup\">");
        str.append("<b>Server base URL:</b>");
        str.append("<br><input name=\"url\" ");
        str.append("value=\"" + host + "\" ");
        str.append("size=\"70\">");
        str.append("<input type=\"hidden\" name=\"setup\" value=\"serverBaseUrl\">");
        str.append("<br><br><input type=\"submit\" value=\"Set up server base URL\">");
        str.append("</form>");

        str.append("<form action=\"setup\">");
        str.append("<input type=\"submit\" value=\"Finish Setup.\">");
        str.append("<input type=\"hidden\" name=\"setup\" value=\"finish\" />");
        str.append("</form>");

        str.append("</body>");
        str.append("</html>");
        return str.toString();
    }
}