package com.seyed.ali.timeentryservice.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("SameReturnValue")
@RestController
@SecurityRequirement(name = "Keycloak")
@RequestMapping("/api/v1/time")
public class TestController {

    @GetMapping("/hello")
    @PreAuthorize("hasRole('board_manager')")
    public String hello() {
        return "Hello World";
    }

}