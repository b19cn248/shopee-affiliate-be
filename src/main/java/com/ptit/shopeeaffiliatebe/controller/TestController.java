package com.ptit.shopeeaffiliatebe.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/tests")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    @GetMapping
    public String test() {
        return "Hello World!";
    }

    @PutMapping("/dm/git")
    public String post() {
        return "Hello World!";
    }
}
