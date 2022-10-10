package com.tikitaka.naechinso.domain.recommend.dto;

import com.tikitaka.naechinso.domain.member.constant.Gender;
import com.tikitaka.naechinso.domain.recommend.entity.Recommend;
import com.tikitaka.naechinso.global.error.ErrorCode;
import com.tikitaka.naechinso.global.error.exception.BadRequestException;
import lombok.*;

/**
 * 유저 초기화면에서 반환할 받은 추천사 정보 DTO
 * */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class RecommendReceiverDTO {
    private String name;

    private Gender gender;

    private int age;

    //추천인의 사진 인증 승인 여부
    private boolean senderCreditAccepted;

    public static RecommendReceiverDTO of(Recommend recommend) {
        if (recommend.getSender() == null) {
            throw new BadRequestException(ErrorCode.RECOMMEND_SENDER_NOT_EXIST);
        }
        return RecommendReceiverDTO.builder()
                .name(recommend.getSenderName())
                .gender(recommend.getSenderGender())
                .age(recommend.getSenderAge())
                .senderCreditAccepted(recommend.getSender().getEduAccepted() || recommend.getSender().getJobAccepted()  )
                .build();
    }
}
