package com.driverr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

    @RequestMapping(value = "/", method = org.springframework.web.bind.annotation.RequestMethod.GET)
    public String index() {
        return "forward:/index.html";
    }

    @RequestMapping(value = "/{path:^(?!api|static).*}", method = org.springframework.web.bind.annotation.RequestMethod.GET)
    public String redirect() {
        return "forward:/index.html";
    }
}
