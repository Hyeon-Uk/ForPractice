package com.hyeonuk.chatting.member.service.auth;

import com.hyeonuk.chatting.member.dto.auth.JoinDto;
import com.hyeonuk.chatting.member.dto.auth.LoginDto;
import com.hyeonuk.chatting.member.dto.MemberDto;
import com.hyeonuk.chatting.member.entity.Member;

public interface MemberAuthService {
    MemberDto save(JoinDto dto);
    MemberDto login(LoginDto dto);

    default Member joinDtoToEntity(JoinDto dto){
        return Member.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .nickname(dto.getNickname())
                .build();
    }

    default MemberDto entityToMemeberDto(Member member){
        return MemberDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();
    }
}