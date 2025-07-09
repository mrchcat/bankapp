package com.github.mrchcat.front.service;

import com.github.mrchcat.front.dto.BankUserDto;
import com.github.mrchcat.front.dto.CashTransactionDto;
import com.github.mrchcat.front.dto.CashTransactionRequestDto;
import com.github.mrchcat.front.dto.EditUserAccountDto;
import com.github.mrchcat.front.dto.FrontBankUserDto;
import com.github.mrchcat.front.dto.FrontRate;
import com.github.mrchcat.front.dto.NewClientRegisterDto;
import com.github.mrchcat.front.dto.NonCashTransfer;
import com.github.mrchcat.front.dto.NonCashTransferRequest;
import com.github.mrchcat.front.dto.UserDetailsDto;
import com.github.mrchcat.front.exception.ExchangeServiceException;
import com.github.mrchcat.front.mapper.FrontMapper;
import com.github.mrchcat.front.model.BankCurrency;
import com.github.mrchcat.front.model.CashAction;
import com.github.mrchcat.front.model.FrontCurrencies;
import com.github.mrchcat.front.security.OAuthHeaderGetter;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import javax.naming.ServiceUnavailableException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FrontServiceImpl implements FrontService {
    private final String ACCOUNT_SERVICE = "bankAccounts";
    private final String ACCOUNTS_REGISTER_NEW_CLIENT_API = "/registration";
    private final String ACCOUNTS_GET_CLIENT_API = "/account";
    private final String ACCOUNTS_PATCH_CLIENT_API = "/account";

    private final String CASH_SERVICE = "bankCash";
    private final String CASH_PROCESS_API = "/cash";

    private final String TRANSFER_SERVICE = "bankTransfer";
    private final String TRANSFER_PROCESS_API = "/transfer";

    private final String EXCHANGE_SERVICE = "bankExchange";
    private final String EXCHANGE_GET_ALL_RATES = "/exchange";

    private final UserDetailsPasswordService userDetailsPasswordService;
    private final UserDetailsService userDetailsService;
    private final RestClient.Builder restClientBuilder;
    private final OAuthHeaderGetter oAuthHeaderGetter;
    private final PasswordEncoder encoder;

    @Override
    public UserDetails editClientPassword(String username, String password) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return userDetailsPasswordService.updatePassword(userDetails, password);
    }

    @Override
    public UserDetails registerNewClient(NewClientRegisterDto ncrdto) throws AuthException {
        String passwordHash = encoder.encode(ncrdto.password());
        var newClientRequestDto = FrontMapper.toCreateNewClientRequestDto(ncrdto, passwordHash);
        var oAuthHeader = oAuthHeaderGetter.getOAuthHeader();
        var response = restClientBuilder.build()
                .post()
                .uri("http://" + ACCOUNT_SERVICE + ACCOUNTS_REGISTER_NEW_CLIENT_API)
                .header(oAuthHeader.name(), oAuthHeader.value())
                .body(newClientRequestDto)
                .retrieve()
                .body(UserDetailsDto.class);
        if (response == null) {
            throw new UsernameNotFoundException("сервис accounts вернул пустой ответ");
        }
        return FrontMapper.toUserDetails(response);
    }

    @Override
    @SneakyThrows
    public FrontBankUserDto getClientDetailsAndAccounts(String username) {
        var oAuthHeader = oAuthHeaderGetter.getOAuthHeader();
        BankUserDto bankUserDto = restClientBuilder.build()
                .get()
                .uri("http://" + ACCOUNT_SERVICE + ACCOUNTS_GET_CLIENT_API + "/" + username)
                .header(oAuthHeader.name(), oAuthHeader.value())
                .retrieve()
                .body(BankUserDto.class);
        if (bankUserDto == null) {
            throw new UsernameNotFoundException("сервис accounts вернул пустой ответ");
        }
        return FrontMapper.toFrontBankUserDto(bankUserDto);
    }

    @Override
    public BankUserDto editUserAccount(String username, EditUserAccountDto editUserAccountDto) throws AuthException {
        System.out.println("должны отправить " + FrontMapper.toRequestDto(editUserAccountDto));
        var oAuthHeader = oAuthHeaderGetter.getOAuthHeader();
        var response = restClientBuilder.build()
                .patch()
                .uri("http://" + ACCOUNT_SERVICE + ACCOUNTS_PATCH_CLIENT_API + "/" + username)
                .header(oAuthHeader.name(), oAuthHeader.value())
                .body(FrontMapper.toRequestDto(editUserAccountDto))
                .retrieve()
                .body(BankUserDto.class);
        if (response == null) {
            throw new UsernameNotFoundException("сервис accounts вернул пустой ответ");
        }
        return response;
    }

    @Override
    public void processCashOperation(String username, CashTransactionDto cashOperationDto, CashAction action) throws AuthException {
        CashTransactionRequestDto requestDto = FrontMapper.toRequestDto(username, cashOperationDto, action);
        var oAuthHeader = oAuthHeaderGetter.getOAuthHeader();
        restClientBuilder.build()
                .post()
                .uri("http://" + CASH_SERVICE + CASH_PROCESS_API)
                .header(oAuthHeader.name(), oAuthHeader.value())
                .body(requestDto)
                .retrieve()
                .body(String.class);
    }

    @Override
    public List<FrontBankUserDto> getAllClientsWithActiveAccounts() throws AuthException, ServiceUnavailableException {
        var oAuthHeader = oAuthHeaderGetter.getOAuthHeader();
        List<BankUserDto> response = restClientBuilder.build()
                .get()
                .uri("http://" + ACCOUNT_SERVICE + ACCOUNTS_GET_CLIENT_API)
                .header(oAuthHeader.name(), oAuthHeader.value())
                .retrieve()
                .body(new ParameterizedTypeReference<List<BankUserDto>>() {
                });
        if (response == null) {
            throw new ServiceUnavailableException("сервис аккаунтов не доступен");
        }
        System.out.println("получили " + response);
        return FrontMapper.toFrontBankUserDto(response);
    }

    @Override
    public void processNonCashOperation(NonCashTransfer nonCashTransfer) throws AuthException {
        NonCashTransferRequest requestDto = FrontMapper.toRequestDto(nonCashTransfer);
        var oAuthHeader = oAuthHeaderGetter.getOAuthHeader();
        restClientBuilder.build()
                .post()
                .uri("http://" + TRANSFER_SERVICE + TRANSFER_PROCESS_API)
                .header(oAuthHeader.name(), oAuthHeader.value())
                .body(requestDto)
                .retrieve()
                .body(String.class);
    }

    @Override
    public List<FrontRate> getAllRates() throws AuthException {
        Map<BankCurrency, BigDecimal> rateMap = getAllRatesFromExchange();
        List<FrontRate> frontRates = new ArrayList<>();
        for (FrontCurrencies.BankFrontCurrency frontCurrency : FrontCurrencies.getCurrencyList()) {
            BankCurrency currency = BankCurrency.valueOf(frontCurrency.name());
            if (rateMap.containsKey(currency)) {
//                frontRates.add(new FrontRate(frontCurrency.name(), frontCurrency.title, rateMap.get(currency)));
            }
        }
        return frontRates;
    }


    private Map<BankCurrency, BigDecimal> getAllRatesFromExchange() throws AuthException {
        var oAuthHeader = oAuthHeaderGetter.getOAuthHeader();
        String requestUrl = "http://" + EXCHANGE_SERVICE + EXCHANGE_GET_ALL_RATES;
        System.out.println("запросили=" + requestUrl);
        try {
            Map<BankCurrency, BigDecimal> rates = restClientBuilder.build()
                    .get()
                    .uri(requestUrl)
                    .header(oAuthHeader.name(), oAuthHeader.value())
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
            if (rates == null) {
                throw new ExchangeServiceException("");
            }
            return rates;
        } catch (Exception ex) {
            throw new ExchangeServiceException("");
        }

    }
}
