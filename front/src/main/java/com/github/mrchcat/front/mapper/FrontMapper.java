package com.github.mrchcat.front.mapper;

import com.github.mrchcat.front.dto.AccountDto;
import com.github.mrchcat.front.dto.BankUserDto;
import com.github.mrchcat.front.dto.CreateNewClientRequestDto;
import com.github.mrchcat.front.dto.EditUserAccountDto;
import com.github.mrchcat.front.dto.EditUserAccountRequestDto;
import com.github.mrchcat.front.dto.FrontAccountDto;
import com.github.mrchcat.front.dto.FrontBankUserDto;
import com.github.mrchcat.front.dto.NewClientRegisterDto;
import com.github.mrchcat.front.dto.UserDetailsDto;
import com.github.mrchcat.front.model.FrontCurrencies;
import net.minidev.json.JSONUtil;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FrontMapper {

    public static UserDetails toUserDetails(UserDetailsDto dto) {
        return User.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .authorities(dto.getAuthorities()
                        .stream()
                        .map(go -> new SimpleGrantedAuthority(go.authority()))
                        .toList()
                )
                .disabled(!dto.isEnabled())
                .accountExpired(!dto.isAccountNonExpired())
                .accountLocked(!dto.isAccountNonLocked())
                .credentialsExpired(!dto.isCredentialsNonExpired())
                .build();
    }

    public static CreateNewClientRequestDto toCreateNewClientRequestDto(NewClientRegisterDto dto, String passwordHash) {
        return CreateNewClientRequestDto.builder()
                .fullName(dto.fullName())
                .email(dto.email())
                .username(dto.login())
                .password(passwordHash)
                .birthDay(dto.birthDate())
                .build();
    }

    public static FrontBankUserDto toFrontDto(BankUserDto dto) {
        return FrontBankUserDto.builder()
                .fullName(dto.fullName())
                .birthDay(dto.birthDay())
                .email(dto.email())
                .accounts(toFrontDto(dto.accounts()))
                .build();
    }

    public static List<FrontAccountDto> toFrontDto(List<AccountDto> dtos) {
        List<FrontAccountDto> frontAccountDtos = new ArrayList<>();
        for (FrontCurrencies.BankCurrency currency : FrontCurrencies.getCurrencyList()) {
            String frontCurrencyStringCode = currency.name();
            AccountDto desiredAccountDto = findFirstByCurrencyCode(frontCurrencyStringCode, dtos);
            FrontAccountDto frontAccountDto = FrontAccountDto.builder()
                    .currencyStringCode(frontCurrencyStringCode)
                    .currencyTitle(currency.title)
                    .isActive(desiredAccountDto != null)
                    .balance(desiredAccountDto != null ? desiredAccountDto.balance() : null)
                    .build();
            frontAccountDtos.add(frontAccountDto);
        }
        return frontAccountDtos;
    }

    private static AccountDto findFirstByCurrencyCode(String frontCurrencyStringCode, List<AccountDto> dtos) {
        for (AccountDto accountDto : dtos) {
            if (accountDto.currencyStringCode().equals(frontCurrencyStringCode)) {
                return accountDto;
            }
        }
        return null;
    }

    public static EditUserAccountRequestDto toRequestDto(EditUserAccountDto dto) {
        System.out.println("внутри Mapper");
        List<String> accountDtos = dto.account();
        Map<String, Boolean> accountMap = FrontCurrencies.getaccountsMap();
        System.out.println("accountMap до равно=" + accountMap);
        if (accountDtos != null && !accountDtos.isEmpty()) {
            System.out.println("внутри цикла");
            for (String currencyStringCode : dto.account()) {
                if (!accountMap.containsKey(currencyStringCode)) {
                    throw new IllegalArgumentException("валюта отсутствует в справочнике");
                }
                accountMap.replace(currencyStringCode, true);
            }
        }
        System.out.println("accountMap после равно=" + accountMap);
        return EditUserAccountRequestDto.builder()
                .fullName(dto.fullName())
                .email(dto.email())
                .accounts(accountMap)
                .build();
    }
}
