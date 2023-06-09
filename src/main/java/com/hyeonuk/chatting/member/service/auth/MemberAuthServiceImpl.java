package com.hyeonuk.chatting.member.service.auth;

import com.hyeonuk.chatting.integ.service.encrypt.PasswordEncoder;
import com.hyeonuk.chatting.member.dto.auth.JoinDto;
import com.hyeonuk.chatting.member.dto.auth.LoginDto;
import com.hyeonuk.chatting.member.dto.MemberDto;
import com.hyeonuk.chatting.member.entity.Member;
import com.hyeonuk.chatting.member.exception.auth.join.AlreadyExistException;
import com.hyeonuk.chatting.member.exception.auth.join.PasswordNotMatchException;
import com.hyeonuk.chatting.member.exception.auth.login.InfoNotMatchException;
import com.hyeonuk.chatting.member.exception.auth.login.RestrictionException;
import com.hyeonuk.chatting.member.repository.MemberRepository;
import com.hyeonuk.chatting.member.entity.MemberSecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberAuthServiceImpl implements MemberAuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional//회원정보와 security
    public MemberDto save(JoinDto dto) {
        if(!dto.getPassword().equals(dto.getPasswordCheck())){
            throw new PasswordNotMatchException("비밀번호가 일치하지 않습니다.");
        }
        //이메일 검증
        memberRepository.findByEmail(dto.getEmail()).ifPresent(member -> {
            throw new AlreadyExistException(member.getEmail()+"은 이미 존재하는 이메일입니다.");
        });

        //닉네임 검증
        memberRepository.findByNickname(dto.getNickname()).ifPresent(member -> {
            throw new AlreadyExistException(member.getNickname()+"은 이미 존재하는 닉네임입니다.");
        });

        //비밀번호 인코딩 SHA256 & salt
        String salt = UUID.randomUUID().toString();
        String encoded = passwordEncoder.encode(dto.getPassword().concat(salt));
        dto.setPassword(encoded);
        MemberSecurity security = MemberSecurity.builder()
                        .salt(salt)//처음 랜덤하게 UUID저장
                        .build();
        dto.setPassword(encoded);//솔트를 적용하여 저장
        dto.setSecurity(security);
        //이메일 발송
        return this.entityToMemeberDto(memberRepository.save(this.joinDtoToEntity(dto)));
    }

    /*
    *
    * 로그인 시에 userCheck부분이 false면 throw exception => 구현 예정
    *
    * 이메일이 존재하지 않거나 패스워드 불일치시 throw exception
    *
    *
    * */
    @Override
    public MemberDto login(LoginDto dto) {
        Member member = memberRepository.findByEmail(dto.getEmail())
                .orElseThrow(()-> new InfoNotMatchException("해당하는 유저가 존재하지 않습니다."));
        //해당 유저의 blockedTime이 안지났다면 throw Exception
        if(member.getMemberSecurity().getBlockedTime()!= null && member.getMemberSecurity().getBlockedTime().compareTo(LocalDateTime.now())>0){
            String format = member.getMemberSecurity().getBlockedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).concat(" 까지 로그인이 불가능합니다.");
            throw new RestrictionException(format);
        }

        String encoded = passwordEncoder.encode(dto.getPassword().concat(member.getMemberSecurity().getSalt()));
        if(!member.getPassword().equals(encoded)){
            member.getMemberSecurity().updateTryCount();
            memberRepository.save(member);
            throw new InfoNotMatchException("비밀번호가 일치하지 않습니다.");
        }
        member.getMemberSecurity().loginSuccess();//로그인 성공으로 초기화
        memberRepository.save(member);
        return entityToMemeberDto(member);
    }
}
