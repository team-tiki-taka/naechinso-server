package com.tikitaka.naechinso.domain.member;

import com.tikitaka.naechinso.domain.card.CardService;
import com.tikitaka.naechinso.domain.card.entity.Card;
import com.tikitaka.naechinso.domain.member.constant.Gender;
import com.tikitaka.naechinso.domain.member.dto.*;
import com.tikitaka.naechinso.domain.member.entity.Member;
import com.tikitaka.naechinso.domain.member.entity.MemberDetail;
import com.tikitaka.naechinso.domain.pending.PendingService;
import com.tikitaka.naechinso.domain.pending.constant.PendingType;
import com.tikitaka.naechinso.domain.pending.dto.PendingFindResponseDTO;
import com.tikitaka.naechinso.domain.pending.dto.PendingUpdateMemberImageRequestDTO;
import com.tikitaka.naechinso.domain.recommend.RecommendRepository;
import com.tikitaka.naechinso.domain.recommend.dto.RecommendReceiverDTO;
import com.tikitaka.naechinso.domain.recommend.entity.Recommend;
import com.tikitaka.naechinso.global.common.request.TokenRequestDTO;
import com.tikitaka.naechinso.global.common.response.TokenResponseDTO;
import com.tikitaka.naechinso.global.config.security.dto.JwtDTO;
import com.tikitaka.naechinso.global.config.security.jwt.JwtTokenProvider;
import com.tikitaka.naechinso.global.error.ErrorCode;
import com.tikitaka.naechinso.global.error.exception.BadRequestException;
import com.tikitaka.naechinso.global.error.exception.ForbiddenException;
import com.tikitaka.naechinso.global.error.exception.NotFoundException;
import com.tikitaka.naechinso.global.error.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final JwtTokenProvider jwtTokenProvider;
    private final PendingService pendingService;
    private final RecommendRepository recommendRepository;
    private final MemberRepository memberRepository;
    private final MemberDetailRepository memberDetailRepository;

    /**
     * ????????? -> Fcm Token DB??? ????????????
     * */
    public MemberLoginResponseDTO login(Member authMember, MemberLoginRequestDTO requestDTO) {
        Member member = findByMember(authMember);

        //?????? ????????? ??? ??????
        if (!StringUtils.isBlank(member.getFcmToken())) {
            throw new BadRequestException(ErrorCode.USER_ALREADY_LOGGED_IN);
        }

        member.setFcmToken(requestDTO.getFcmToken());
        memberRepository.save(member);

        return new MemberLoginResponseDTO(requestDTO.getFcmToken());
    }

    /**
     * ?????? ????????? -> Fcm Token ??? ????????????
     * */
    public MemberLoginResponseDTO forceLogin(Member authMember, MemberLoginRequestDTO requestDTO) {
        Member member = findByMember(authMember);

        member.setFcmToken(requestDTO.getFcmToken());
        memberRepository.save(member);

        return new MemberLoginResponseDTO(requestDTO.getFcmToken());
    }

    /**
     * ???????????? -> Register Token ??? Fcm Token DB?????? ????????????
     * */
    public MemberLoginResponseDTO logout(Member authMember) {
        Member member = findByMember(authMember);

        //?????? ???????????? ??? ??????
        if (StringUtils.isBlank(member.getFcmToken())) {
            throw new BadRequestException(ErrorCode.USER_ALREADY_LOGGED_OUT);
        }

        //redis ?????? registerToken ??????
        jwtTokenProvider.deleteRegisterToken(member.getPhone());

        //?????? ?????? ?????? ??????
        member.setFcmToken("");
        memberRepository.save(member);

        return new MemberLoginResponseDTO("");
    }

    /**
     * ????????? -> Fcm Token DB??? ????????????
     * @// TODO: 2022/10/30 ????????? ??????
     * */
    public MemberReissueResponseDTO reissue(String accessToken, String refreshToken) {
        String phone;

        if (!jwtTokenProvider.validateTokenExceptExpiration(accessToken)){
            throw new BadRequestException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        try {
            phone = jwtTokenProvider.parseClaims(accessToken).getSubject();
        } catch (Exception e) {
            throw new BadRequestException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Member authMember = findByPhone(phone);

        //?????? ?????? (detail == null) ?????? ??? ?????? ????????? ?????????
        final Boolean hasDetail = authMember.getDetail() != null;

        //?????? ???????????? ???????????? ?????? ?????? ????????? ?????????
        List<RecommendReceiverDTO> recommendReceived;
        if (!hasDetail) {
            recommendReceived = recommendRepository.findAllByReceiverPhoneAndSenderNotNull(phone)
                    .stream().map(RecommendReceiverDTO::of).collect(Collectors.toList());
        } else {
            recommendReceived = null;
        }

        //?????? ??? ?????? ????????????
        final Boolean isBanned = false;

        jwtTokenProvider.validateRefreshToken(phone, refreshToken);

        TokenResponseDTO tokenResponseDTO = jwtTokenProvider.generateToken(new JwtDTO(phone, authMember.getRole().toString()));

        return MemberReissueResponseDTO.builder()
                .accessToken(tokenResponseDTO.getAccessToken())
                .refreshToken(tokenResponseDTO.getRefreshToken())
                .recommendReceived(recommendReceived)
                .isActive(hasDetail)
                .isBanned(isBanned)
                .build();
    }


    public MemberCommonJoinResponseDTO joinCommonMember(String phone, MemberCommonJoinRequestDTO dto) {
        //?????? ???????????? ????????? ?????? 400
        Optional<Member> checkMember = memberRepository.findByPhone(phone);
        if(checkMember.isPresent()) {
            throw new BadRequestException(ErrorCode.USER_ALREADY_EXIST);
        }

        Member member = MemberCommonJoinRequestDTO.toCommonMember(phone, dto);
        memberRepository.save(member);

        //??? ????????? ????????? ????????? ??????
        List<Recommend> recommendList = recommendRepository.findAllByReceiverPhone(phone);
        recommendList.forEach(recommend -> recommend.setReceiver(member));

        TokenResponseDTO tokenResponseDTO
                = jwtTokenProvider.generateToken(new JwtDTO(phone, "ROLE_USER"));

        return MemberCommonJoinResponseDTO.of(member, tokenResponseDTO);
    }

    /**
     * @// TODO: 2022-10-07 ???????????? ?????? ?????? 
     * */
    public MemberCommonJoinResponseDTO updateCommonMember(Member authMember, MemberUpdateCommonRequestDTO dto) {
        //?????? ????????? 404
        Member member = findByMember(authMember);
        member.updateCommon(dto);
        memberRepository.save(member);

        TokenResponseDTO tokenResponseDTO
                = jwtTokenProvider.generateToken(new JwtDTO(member.getPhone(), "ROLE_USER"));

        return MemberCommonJoinResponseDTO.of(member);
    }

    public MemberDetailResponseDTO readDetail(Member member) {
        return MemberDetailResponseDTO.of(member);
    }


    /**
     *  ???????????? ?????? ????????? ?????? ?????? ??????
     * @// TODO: 2022/11/05 ?????? ????????? ????????? ??????
     **/
    public MemberDetailResponseDTO createDetail(Member authMember, MemberDetailJoinRequestDTO dto) {
        //????????? ????????? ?????? fetch
        Member member = findByMember(authMember);

        //detail ????????? ????????? ?????? ????????? ??????
        if (member.getDetail() != null) {
            throw new BadRequestException(ErrorCode.USER_ALREADY_EXIST);
        }

        //?????? ?????? ????????? ?????? ??????
        if (member.getRecommendReceived() == null || member.getRecommendReceived().isEmpty()) {
            throw new ForbiddenException(ErrorCode.RECOMMEND_NOT_RECEIVED);
        }

        for (Recommend recommend : member.getRecommendReceived()) {
            //?????? ???????????? ?????? ????????? ??????
            if (recommend.getSender() == null) {
                continue;
            }

            //?????? ????????? ??? ??????????????? ????????? ????????? ????????? ????????? ?????? ??????
            if (recommend.getSender().getJobAccepted() || recommend.getSender().getEduAccepted()) {
                member.updateCommonInfo(dto);
                memberRepository.save(member);

                MemberDetail detail = MemberDetail.of(member, dto);
                memberDetailRepository.save(detail);

                member.setDetail(detail);

                /** ?????? ?????? 221105 */
//                pendingService.createPendingByMemberImage(member, new MemberUpdateImageRequestDTO(dto.getImages()));
                member.updateImage(dto.getImages());
                /** ?????? ?????? 221105 */

                return MemberDetailResponseDTO.of(detail);
            }
        }
        //???????????? ????????? ????????? ????????? ???????????? ?????? ??????
        throw new UnauthorizedException(ErrorCode.RECOMMEND_SENDER_UNAUTHORIZED);
    }


    /**
     * ?????? ?????? ???????????? ?????? ??????
     * ?????? ????????? Pending ?????? ?????? ??? ????????????
     * */
    public MemberCommonResponseDTO updateJobRequest(Member authMember, MemberUpdateJobRequestDTO dto){
        //?????? ?????? ?????? ??????
//        return pendingService.createPendingByJob(authMember, dto);
        /** ?????? ?????? 221105 */
        Member member = memberRepository.findByMember(authMember)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        member.updateJob(dto);
        return MemberCommonResponseDTO.of(member);
        /** ?????? ?????? 221105 */
    }

    /**
     * ?????? ?????? ???????????? ?????? ??????
     * ?????? ????????? Pending ?????? ?????? ??? ????????????
     * */
    public MemberCommonResponseDTO updateEduRequest(Member authMember, MemberUpdateEduRequestDTO dto){
        //?????? ?????? ?????? ??????
//        return pendingService.createPendingByEdu(authMember, dto);
        /** ?????? ?????? 221105 */
        Member member = memberRepository.findByMember(authMember)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        member.updateEdu(dto);
        return MemberCommonResponseDTO.of(member);
        /** ?????? ?????? 221105 */
    }

    /**
     * ?????? ?????? ????????? ????????????
     * */
    public MemberAcceptsResponseDTO updateAccepts(Member authMember, MemberUpdateAcceptsRequestDTO dto){
        Member member = findByMember(authMember);
        member.updateAccepts(dto);
        memberRepository.save(member);
        return MemberAcceptsResponseDTO.of(member);
    }

    /**
     * MemberDetail ??? ????????? ???????????? ????????? ??????
     * */
    public MemberDetailResponseDTO updateImage(Member authMember, MemberUpdateImageRequestDTO dto){
        /** ?????? ?????? 221105 */
//        return pendingService.createPendingByMemberImage(authMember, dto);
        Member member = memberRepository.findByMember(authMember)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        member.updateImage(dto.getImages());
        return MemberDetailResponseDTO.of(member);
        /** ?????? ?????? 221105 */
    }

    /**
     * soft delete (?????? ??????)
     * */
    public Member delete(Member authMember){
        Member member = findByMember(authMember);

        member.setDeleted();
        member.getDetail().setDeleted();
        memberRepository.save(member);

        return member;
    }

    public void validateToken(Member authMember) {
        if (authMember == null) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_USER);
        }
    }

    public void validateFormalMember(Member authMember) {
        validateToken(authMember);
        if (authMember.getDetail() == null) {
            throw new UnauthorizedException(ErrorCode.FORBIDDEN_USER);
        }
    }

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    /** ?????? ???????????? ???????????? ?????? ?????? ID ??? ???????????? ?????????
     * ???????????? ????????? ?????? ?????? ?????? ????????? ???????????? */
    public Member findTopByIdNotInAndGenderNotAndDetailNotNull(Collection<Long> ids, Gender gender) {
        List<Member> memberList = memberRepository.findByIdNotInAndGenderNotAndDetailNotNull(ids, gender);
        if (memberList.isEmpty()) {
            throw new NotFoundException(ErrorCode.RANDOM_USER_NOT_FOUND);
        }
        return memberList.get(new Random().nextInt(memberList.size()));
    }

    public boolean existsById(Long memberId) {
        return memberRepository.existsById(memberId);
    }


    public Member findByPhone(String phone) {
        return memberRepository.findByPhone(phone)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    public Member findByMember(Member member) {
        return findByPhone(member.getPhone());
    }

    public List<MemberFindResponseDTO> findAll() {
        return memberRepository.findAll().stream()
                .map(MemberFindResponseDTO::of).collect(Collectors.toList());
    }

    public MemberCommonResponseDTO readCommonMember(Member authMember) {
        Member member = findByMember(authMember);
        return MemberCommonResponseDTO.of(member);
    }
}
