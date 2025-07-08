package com.github.mrchcat.exchange_generator.controller;

import com.github.mrchcat.exchange_generator.service.GeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GenController {
    private final GeneratorService generatorService;

    @GetMapping("/generator")
    String test() {
        generatorService.sendNewRates();
        return "test";
    }
}
