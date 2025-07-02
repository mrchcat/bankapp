package com.github.mrchcat.profile.controller;

import com.github.mrchcat.profile.service.BlockerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BlockerController {
    private final BlockerService blockerService;


}
