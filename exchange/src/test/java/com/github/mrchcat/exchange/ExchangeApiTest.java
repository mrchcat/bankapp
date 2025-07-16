package com.github.mrchcat.exchange;

import com.github.mrchcat.shared.enums.BankCurrency;
import com.github.mrchcat.shared.exchange.CurrencyExchangeRatesDto;
import com.github.mrchcat.shared.exchange.CurrencyRate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

//сервис нужно тестировать с "mvn clean install", чтобы стабы попали в каталог maven
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureStubRunner(
        ids = "com.github.mrchcat:exchange:+:stubs:8086",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
public class ExchangeApiTest {
    @LocalServerPort
    int port;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void sendRatesToExchange() throws Exception {
        var USDrate = CurrencyRate.builder()
                .currency(BankCurrency.USD)
                .buyRate(BigDecimal.valueOf(151))
                .sellRate(BigDecimal.valueOf(161))
                .time(LocalDateTime.parse("2025-07-08T17:37:26.666306300"))
                .build();
        var CNYrate = CurrencyRate.builder()
                .currency(BankCurrency.CNY)
                .buyRate(BigDecimal.valueOf(15))
                .sellRate(BigDecimal.valueOf(16))
                .time(LocalDateTime.parse("2025-07-08T17:37:26.666306300"))
                .build();
        List<CurrencyRate> ratesList = List.of(USDrate, CNYrate);
        var rates = new CurrencyExchangeRatesDto(BankCurrency.RUB, ratesList);
        String url = "http://localhost:" + port + "/exchange";

        ResponseEntity<Void> response = new RestTemplate().postForEntity(url,rates,Void.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//        final ObjectMapper mapper = new ObjectMapper();
//        final String jsonContent = mapper.writeValueAsString(rates);
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post(url)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonContent))
//                .andExpect(status().isNoContent());
    }
}
