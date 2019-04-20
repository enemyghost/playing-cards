package com.gmo.big2.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {
    /**
     * Default endpoint, tells the ELB that the server is healthy.
     */
    @GetMapping(value = "/")
    public ResponseEntity<String> ok() {
        return ResponseEntity.ok("OK");
    }
}
