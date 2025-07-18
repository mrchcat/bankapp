package com.github.mrchcat.front.mapper;

import com.github.mrchcat.front.dto.FrontAccountDto;
import com.github.mrchcat.front.dto.FrontBankUserDto;
import com.github.mrchcat.front.dto.FrontCashTransactionDto;
import com.github.mrchcat.front.dto.FrontEditUserAccountDto;
import com.github.mrchcat.front.dto.NewClientRegisterDto;
import com.github.mrchcat.front.dto.NonCashTransfer;
import com.github.mrchcat.front.dto.UserDetailsDto;
import com.github.mrchcat.front.model.FrontCurrencies;
import com.github.mrchcat.shared.accounts.AccountDto;
import com.github.mrchcat.shared.accounts.BankUserDto;
import com.github.mrchcat.shared.accounts.CreateNewClientDto;
import com.github.mrchcat.shared.accounts.EditUserAccountDto;
import com.github.mrchcat.shared.cash.CashTransactionDto;
import com.github.mrchcat.shared.enums.CashAction;
import com.github.mrchcat.shared.transfer.NonCashTransferDto;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
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

    public static CreateNewClientDto toCreateNewClientRequestDto(NewClientRegisterDto dto, String passwordHash) {
        return CreateNewClientDto.builder()
                .fullName(dto.fullName())
                .email(dto.email())
                .username(dto.login())
                .password(passwordHash)
                .birthDay(dto.birthDate())
                .build();
    }

    public static FrontBankUserDto toFrontBankUserDto(BankUserDto dto) {
        return FrontBankUserDto.builder()
                .username(dto.username())
                .fullName(dto.fullName())
                .birthDay(dto.birthDay())
                .email(dto.email())
                .accounts((dto.accounts() == null) ? null : FrontMapper.toFrontAccountDto(dto.accounts()))
                .build();
    }

    public static List<FrontBankUserDto> toFrontBankUserDto(List<BankUserDto> list) {
        return list.stream()
                .map(FrontMapper::toFrontBankUserDto)
                .toList();
    }

    public static List<FrontAccountDto> toFrontAccountDto(List<AccountDto> dtos) {
        List<FrontAccountDto> frontAccountDtos = new ArrayList<>();
        for (FrontCurrencies.BankFrontCurrency currency : FrontCurrencies.getCurrencyList()) {
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

    public static EditUserAccountDto toRequestDto(FrontEditUserAccountDto dto) {
        List<String> accountDtos = dto.account();
        Map<String, Boolean> accountMap = FrontCurrencies.getaccountsMap();
        if (accountDtos != null && !accountDtos.isEmpty()) {
            for (String currencyStringCode : dto.account()) {
                if (!accountMap.containsKey(currencyStringCode)) {
                    throw new IllegalArgumentException("валюта отсутствует в справочнике");
                }
                accountMap.replace(currencyStringCode, true);
            }
        }
        return EditUserAccountDto.builder()
                .fullName(dto.fullName())
                .email(dto.email())
                .accounts(accountMap)
                .build();
    }

    public static CashTransactionDto toRequestDto(String username, FrontCashTransactionDto cashOperationDto, CashAction operationType) {
        return CashTransactionDto.builder()
                .username(username)
                .value(cashOperationDto.value())
                .currency(cashOperationDto.accountCurrency())
                .action(operationType)
                .build();
    }

    public static NonCashTransferDto toRequestDto(NonCashTransfer dto) {
        return NonCashTransferDto.builder()
                .direction(dto.direction())
                .fromCurrency(dto.fromCurrency())
                .toCurrency(dto.toCurrency())
                .amount(dto.amount())
                .fromUsername(dto.fromUsername())
                .toUsername(dto.toUsername())
                .build();
    }

}
