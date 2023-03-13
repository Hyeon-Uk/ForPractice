package com.hyeonuk.chatting.member.repository;

import com.hyeonuk.chatting.member.dto.JoinDto;
import com.hyeonuk.chatting.member.dto.MemberDto;
import com.hyeonuk.chatting.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByNickname(String nickname);
}
