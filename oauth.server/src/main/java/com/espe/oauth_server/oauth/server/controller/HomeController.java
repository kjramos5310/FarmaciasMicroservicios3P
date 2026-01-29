package com.espe.oauth_server.oauth.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, String> home() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "OAuth2 Authorization Server");
        response.put("status", "Running");
        response.put("port", "9000");
        return response;
    }
}
