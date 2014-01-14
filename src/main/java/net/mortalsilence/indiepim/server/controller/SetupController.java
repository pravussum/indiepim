package net.mortalsilence.indiepim.server.controller;

import org.springframework.stereotype.Controller;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 18.08.13
 * Time: 23:25
 * To change this template use File | Settings | File Templates.
 */

@Controller
public interface SetupController {
    public String get(String action, String keystorePath, String url, String host) throws IOException;
}

