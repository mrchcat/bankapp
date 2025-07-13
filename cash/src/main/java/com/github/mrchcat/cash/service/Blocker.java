package com.github.mrchcat.cash.service;

import com.github.mrchcat.cash.repository.CashRepository;
import com.github.mrchcat.cash.security.OAuthHeaderGetter;
import com.github.mrchcat.shared.blocker.BlockerResponseDto;
import com.github.mrchcat.shared.cash.CashTransactionDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import javax.naming.ServiceUnavailableException;

@Component
@RequiredArgsConstructor
public class Blocker {
    private final String BLOCKER_SERVICE = "bankBlocker";
    private final String BLOCKER_ASK_PERMISSION = "/blocker/cash";

    private final RestClient.Builder restClientBuilder;
    private final OAuthHeaderGetter oAuthHeaderGetter;

    @CircuitBreaker(name = "blocker", fallbackMethod = "fallbackBlocker")
    @Retry(name = "blocker", fallbackMethod = "fallbackBlocker")
    public BlockerResponseDto checkCashTransaction(CashTransactionDto cashTransactionDto) throws AuthException, ServiceUnavailableException {
        System.out.println("блокер");
        var oAuthHeader = oAuthHeaderGetter.getOAuthHeader();
        var blockerResponse = restClientBuilder.build()
                .post()
                .uri("http://" + BLOCKER_SERVICE + BLOCKER_ASK_PERMISSION)
                .header(oAuthHeader.name(), oAuthHeader.value())
                .body(cashTransactionDto)
                .retrieve()
                .body(BlockerResponseDto.class);
        if (blockerResponse == null) {
            throw new ServiceUnavailableException("сервис подтверждения не доступен");
        }
        return blockerResponse;
    }

    private BlockerResponseDto fallbackBlocker(Throwable t) {
        return new BlockerResponseDto(false, "сервис подтверждения не доступен");
    }

}
