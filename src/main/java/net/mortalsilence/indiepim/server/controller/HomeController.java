package net.mortalsilence.indiepim.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 18.08.13
 * Time: 22:50
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class HomeController {

    @RequestMapping(value="/")
    public String home(){
        return "index";
    }
}
