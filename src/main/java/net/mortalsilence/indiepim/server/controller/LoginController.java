package net.mortalsilence.indiepim.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@SuppressWarnings("serial")
@Controller
public class LoginController {

    @RequestMapping(value = "/login")
    public String login() {
        return "login";
    }

//	// TODO write fieldVerifier for ServiceInputs , escapeHTML against XS-Vulnerabilities!
//	// encapsulate tx handling
//
//    final GenericLoginService loginService = new GenericLoginService();
//
//
//	public void addUser(String userName, String password) {
//        loginService.addUser(userName, password);
//	}
//
//	public String getSession(final String userName, final String password) {
//        return loginService.getSession(userName, password, getThreadLocalRequest().getSession());
//	}
//
//	public Boolean isSessionValid(final String sessionId) {
//		return loginService.isSessionValid(sessionId);
//	}
//
//    public Boolean isSetupDone() {
//        return loginService.isSetupDone();
//    }
}
