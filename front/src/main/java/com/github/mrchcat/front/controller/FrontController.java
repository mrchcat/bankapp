package com.github.mrchcat.front.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class FrontController {

    @GetMapping("/main")
    String getMain() {
        return "/main";
    }

}
