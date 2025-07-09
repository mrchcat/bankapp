package com.github.mrchcat.front.controller;

import com.github.mrchcat.front.dto.FrontRate;
import com.github.mrchcat.front.model.BankCurrency;
import com.github.mrchcat.front.model.FrontCurrencies;
import com.github.mrchcat.front.service.FrontService;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class RatesController {
    private final FrontService frontService;

    /**
     * контроллер для получения данных о курсах
     */
    @GetMapping("/front/rates")
    List<FrontRate> getAllRates() throws AuthException {
        List<FrontRate> list = new ArrayList<>();
        list.add(new FrontRate("USD", "доллар", BigDecimal.ONE, BigDecimal.TEN));
        list.add(new FrontRate("CNY", "юани", BigDecimal.TEN,BigDecimal.ZERO));
        return list;
        //        return frontService.getAllRates();
    }
}
