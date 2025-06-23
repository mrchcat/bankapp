package com.github.mrchcat.front.mapper;

import com.github.mrchcat.front.dto.GrantedAuthorityDto;
import com.github.mrchcat.front.dto.UserDetailsDto;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class FrontMapper {

    public static UserDetails toUserDetails(UserDetailsDto dto){
        return User.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .authorities(FrontMapper.toGrantedAuthority(dto.getAuthorities()))
                .disabled(!dto.isEnabled())
                .accountExpired(!dto.isAccountNonExpired())
                .accountLocked(!dto.isAccountNonLocked())
                .credentialsExpired(!dto.isCredentialsNonExpired())
                .build();
    }

    private static Collection<SimpleGrantedAuthority> toGrantedAuthority(List<GrantedAuthorityDto> list){
        return list.stream()
                .map(dto->new SimpleGrantedAuthority(dto.getAuthority()))
                .toList();
    }
}
