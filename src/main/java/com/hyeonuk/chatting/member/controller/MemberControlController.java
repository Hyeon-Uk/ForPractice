package com.hyeonuk.chatting.member.controller;

import com.hyeonuk.chatting.integ.util.ApiUtils;
import com.hyeonuk.chatting.member.dto.MemberDto;
import com.hyeonuk.chatting.member.dto.control.FriendAddDto;
import com.hyeonuk.chatting.member.dto.control.MemberSearchDto;
import com.hyeonuk.chatting.member.service.control.MemberControlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Controller
@RequestMapping("/api/member")
@Slf4j
@RequiredArgsConstructor
public class MemberControlController {
    private final MemberControlService memberControlService;

    /*
    *
    * 이름을 포함한 유저 찾기
    * JSON형태로 return
    * */
    @ResponseBody
    @GetMapping
    public ResponseEntity<ApiUtils.ApiResult<List<MemberDto>>> findMemberByNickname(@Validated MemberSearchDto dto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            throw new IllegalArgumentException(bindingResult.getFieldError().getDefaultMessage());
        }
        return ApiUtils.success(memberControlService.findAllByNickname(dto),HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping
    public ResponseEntity<ApiUtils.ApiResult<Boolean>> addMember(@SessionAttribute("member")MemberDto member, @RequestBody FriendAddDto dto){
        memberControlService.addFriend(member,MemberDto.builder().id(dto.getId()).build());
        return ApiUtils.success(true,HttpStatus.OK);
    }
}
