package com.github.mrchcat.front.mapper;

import com.github.mrchcat.front.dto.CreateNewClientRequestDto;
import com.github.mrchcat.front.dto.GrantedAuthorityDto;
import com.github.mrchcat.front.dto.NewClientRegisterDto;
import com.github.mrchcat.front.dto.UserDetailsDto;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

}
