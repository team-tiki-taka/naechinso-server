package com.tikitaka.naechinso.domain.card.dto;

import com.tikitaka.naechinso.domain.member.constant.Gender;
import com.tikitaka.naechinso.domain.member.entity.Member;
import com.tikitaka.naechinso.domain.member.entity.MemberDetail;
import com.tikitaka.naechinso.domain.recommend.entity.Recommend;
import com.tikitaka.naechinso.global.error.ErrorCode;
import com.tikitaka.naechinso.global.error.exception.NotFoundException;
import com.tikitaka.naechinso.global.util.CustomStringUtil;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class CardOppositeMemberProfileResponseDTO {

    private Long targetMemberId;

    private List<String> images;
    private String name;
    private int age;

    private String address;
    private Gender gender;

    private String jobName;
    private String jobPart;
    private String jobLocation;

    private String eduName;
    private String eduMajor;
    private String eduLevel;

    private List<String> personalities;
    private String religion;
    private int height;
    private String smoke;
    private String drink;

    private String hobby;
    private String style;
    private String introduce;

    private String mbti;
    private Recommendation recommend;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    @ToString
    public static class Recommendation {
        private String name;
        private Gender gender;
        private List<String> appeals;

        private String jobName;
        private String jobPart;
        private String jobLocation;
        private String eduName;
        private String eduMajor;
        private String eduLevel;

        private String meet;
        private String period;
        private String appealDetail;

        public static Recommendation of(Recommend recommend) {
            Member sender = recommend.getSender();
            return Recommendation.builder()
                    .name(CustomStringUtil.hideName(recommend.getSenderName()))
                    .gender(recommend.getSenderGender())
                    .appeals(recommend.getReceiverAppeals())
                    .appealDetail(recommend.getReceiverAppealDetail())
                    .eduName(sender.getEduName())
                    .eduMajor(sender.getEduMajor())
                    .eduLevel(sender.getEduLevel())
                    .jobName(sender.getJobName())
                    .jobPart(sender.getJobPart())
                    .jobLocation(sender.getJobLocation())
                    .meet(recommend.getReceiverMeet())
                    .period(recommend.getReceiverPeriod())
                    .build();
        }
    }

    public static CardOppositeMemberProfileResponseDTO of(Member member) {
        MemberDetail detail = member.getDetail();
        //????????? ?????? ????????? ??????
        if (detail == null) {
            throw new NotFoundException(ErrorCode.USER_NOT_SIGNED_UP);
        }

        //???????????? ??? ?????? ????????? ???????????? ?????? ??????
        if (member.getRecommendReceived() == null){
            throw new NotFoundException(ErrorCode.RECOMMEND_NOT_FOUND);
        }

        for (Recommend recommend: member.getRecommendReceived()) {
            //?????? ?????? ??????
            if (recommend.getSender() != null && recommend.getReceiver() != null) {
                return CardOppositeMemberProfileResponseDTO.builder()
                        .targetMemberId(member.getId())
                        .images(detail.getImages())
                        .name(CustomStringUtil.hideName(member.getName()))
                        .age(member.getAge())
                        .address(detail.getAddress())
                        .jobName(member.getJobName())
                        .jobPart(member.getJobPart())
                        .jobLocation(member.getJobLocation())
                        .eduName(member.getEduName())
                        .eduMajor(member.getEduMajor())
                        .eduLevel(member.getEduLevel())
                        .gender(member.getGender())
                        .personalities(detail.getPersonalities())
                        .religion(detail.getReligion())
                        .height(detail.getHeight())
                        .smoke(detail.getSmoke())
                        .drink(detail.getDrink())
                        .hobby(detail.getHobby())
                        .style(detail.getStyle())
                        .introduce(detail.getIntroduce())
                        .mbti(detail.getMbti())
                        .recommend(CardOppositeMemberProfileResponseDTO.Recommendation.of(recommend))
                        .build();

            }
        }
        throw new NotFoundException(ErrorCode.RECOMMEND_NOT_FOUND);
    }
}
