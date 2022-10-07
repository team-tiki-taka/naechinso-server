package com.tikitaka.naechinso.domain.member;

import com.tikitaka.naechinso.domain.member.dto.*;
import com.tikitaka.naechinso.domain.member.entity.Member;
import com.tikitaka.naechinso.domain.recommend.RecommendService;
import com.tikitaka.naechinso.domain.recommend.dto.RecommendMemberAcceptRequestDTO;
import com.tikitaka.naechinso.domain.recommend.dto.RecommendMemberAcceptAndUpdateRequestDTO;
import com.tikitaka.naechinso.domain.recommend.dto.RecommendResponseDTO;
import com.tikitaka.naechinso.global.annotation.AuthMember;
import com.tikitaka.naechinso.global.config.CommonApiResponse;
import com.tikitaka.naechinso.global.config.security.jwt.JwtTokenProvider;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final RecommendService recommendService;
    private final JwtTokenProvider jwtTokenService;


    @GetMapping
    @ApiOperation(value = "유저 자신의 모든 정보를 가져온다 (AccessToken)")
    public CommonApiResponse<MemberCommonResponseDTO> getMyInformation(
            @ApiIgnore @AuthMember Member member
    ) {
        return CommonApiResponse.of(MemberCommonResponseDTO.of(member));
    }


    @GetMapping("/detail")
    @ApiOperation(value = "회원가입 세부 정보를 가져온다 (AccessToken 필요)")
    public CommonApiResponse<MemberDetailResponseDTO> getMemberDetail(
            @ApiIgnore @AuthMember Member member
    ) {
        final MemberDetailResponseDTO res = memberService.readDetail(member);
        return CommonApiResponse.of(res);
    }


    @PostMapping("/join")
    @ApiOperation(value = "유저를 공통 정보로 가입시킨다 (RegisterToken)")
    public CommonApiResponse<MemberCommonJoinResponseDTO> joinCommonMember(
            HttpServletRequest request,
            @Valid @RequestBody MemberCommonJoinRequestDTO dto
    ) {
        String phone = jwtTokenService.parsePhoneByRegisterToken(request);
        return CommonApiResponse.of(memberService.joinCommonMember(phone, dto));
    }

    @PatchMapping("/common")
    @ApiOperation(value = "유저를 공통 정보를 수정한다 (AccessToken)")
    public CommonApiResponse<MemberCommonJoinResponseDTO> updateCommonMember(
            @Valid @RequestBody MemberUpdateCommonRequestDTO dto,
            @ApiIgnore @AuthMember Member member
    ) {
        return CommonApiResponse.of(memberService.updateCommonMember(member, dto));
    }

    @PatchMapping("/job")
    @ApiOperation(value = "직업 정보를 업데이트 한다 (AccessToken)")
    public CommonApiResponse<MemberCommonResponseDTO> setMemberJob(
            @RequestBody MemberJobUpdateRequestDTO dto,
            @ApiIgnore @AuthMember Member member
    ) {
        return CommonApiResponse.of(memberService.updateJob(member, dto));
    }

    @PatchMapping("/edu")
    @ApiOperation(value = "학력 정보를 업데이트 한다 (AccessToken)")
    public CommonApiResponse<MemberCommonResponseDTO> setMemberJob(
            @RequestBody MemberEduUpdateRequestDTO dto,
            @ApiIgnore @AuthMember Member member
    ) {
        return CommonApiResponse.of(memberService.updateEdu(member, dto));
    }




    @PostMapping("/join/detail")
    @ApiOperation(value = "회원가입 세부 정보를 입력하여 최종 가입시킨다 (AccessToken)")
    public CommonApiResponse<MemberDetailResponseDTO> setMemberDetail(
            @Valid @RequestBody MemberDetailJoinRequestDTO dto,
            @ApiIgnore @AuthMember Member member
    ) {
        final MemberDetailResponseDTO res = memberService.createDetail(member, dto);
        return CommonApiResponse.of(res);
    }

    //페이징 처리 추가할 예정
    @GetMapping("/find")
    @ApiOperation(value = "[Admin]현재 가입한 모든 유저를 불러온다 (AccessToken)")
    public CommonApiResponse<List<MemberCommonResponseDTO>> getMyInformation(
//            @RequestBody RecommendMemberAcceptRequestDTO dto,
//            @ApiIgnore @AuthMember Member member
    ) {
        return CommonApiResponse.of(memberService.findAll());
    }
}
