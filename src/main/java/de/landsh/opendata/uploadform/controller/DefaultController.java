package de.landsh.opendata.uploadform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class DefaultController {
    @GetMapping("/")
    public String index(Principal principal) {
        if( principal != null) {
            return "/admin";
        } else  {
            return "/index";
        }
    }
    @GetMapping("/login")
    public String login() {
        return "/login";
    }
}
