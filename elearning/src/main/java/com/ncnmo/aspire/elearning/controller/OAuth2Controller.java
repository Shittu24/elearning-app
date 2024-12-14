package com.ncnmo.aspire.elearning.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class OAuth2Controller {

    @GetMapping("/oauth2/success")
    public Map<String, String> oAuth2Success(@RequestParam("token") String token) {
        // Return the token in a JSON response
        return Map.of("token", token);
    }
}

