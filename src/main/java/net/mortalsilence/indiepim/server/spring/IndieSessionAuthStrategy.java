package net.mortalsilence.indiepim.server.spring;

import net.mortalsilence.indiepim.server.comet.CometService;
import net.mortalsilence.indiepim.server.security.IndieUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IndieSessionAuthStrategy extends ConcurrentSessionControlAuthenticationStrategy {

    @Inject CometService cometService;

    public IndieSessionAuthStrategy(SessionRegistry sessionRegistry) {
        super(sessionRegistry);
    }



//    @Override
//    protected void onSessionChange(String originalSessionId, HttpSession newSession, Authentication auth) {
//        super.onSessionChange(originalSessionId, newSession, auth);
//        cometService.transferSessionCometMsgQueue(((IndieUser) auth.getPrincipal()).getId(), originalSessionId, newSession.getId());
//    }

    @Override
    public void onAuthentication(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        super.onAuthentication(authentication, request, response);
        cometService.addSession(((IndieUser) authentication.getPrincipal()).getId(), request.getSession().getId());
    }
}
