package com.tikitaka.naechinso.domain.recommend.dto;

import com.tikitaka.naechinso.domain.member.constant.Gender;
import com.tikitaka.naechinso.domain.member.dto.MemberEduUpdateRequestDTO;
import com.tikitaka.naechinso.domain.member.dto.MemberJobUpdateRequestDTO;
import com.tikitaka.naechinso.domain.member.entity.Member;
import com.tikitaka.naechinso.global.annotation.Enum;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.*;

/** 가입하지 않은 멤버가 추천사를 써줄 때 요청 DTO
 * @author gengminy 221001
*/
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class RecommendBySenderRequestDTO {
//    @ApiModelProperty(example = "닉")
//    @NotBlank(message = "이름을 입력해주세요")
//    private String name;
//
//    @ApiModelProperty(example = "M")
//    @Enum(enumClass = Gender.class, message = "성별 입력이 올바르지 않습니다. M 또는 W가 필요합니다")
//    private Gender gender;
//
//    @ApiModelProperty(example = "25")
//    @Min(value = 25, message = "25-33세까지만 가입 가능합니다")
//    @Max(value = 33, message = "25-33세까지만 가입 가능합니다")
//    private int age;
//
//    @NotNull(message = "서비스 이용약관 동의가 필요합니다")
//    @AssertTrue(message = "서비스 이용약관 동의가 필요합니다")
//    private boolean acceptsService;
//
//    @NotNull(message = "개인정보 이용 동의가 필요합니다")
//    @AssertTrue(message = "개인정보 이용 동의가 필요합니다")
//    private boolean acceptsInfo;
//
//    @NotNull(message = "종교 정보 제공 동의가 필요합니다")
//    @AssertTrue(message = "종교 정보 제공 동의가 필요합니다")
//    private boolean acceptsReligion;
//
//    @NotNull(message = "위치 정보 제공 동의 여부가 필요합니다")
//    private boolean acceptsLocation;
//
//    @NotNull(message = "마케팅 동의 여부가 필요합니다")
//    private boolean acceptsMarketing;
//
//    private MemberEduUpdateRequestDTO edu;
//
//    private MemberJobUpdateRequestDTO job;
//
//
//


    @ApiModelProperty(example = "01099999999")
    @NotBlank(message = "친구의 전화번호를 입력해주세요")
    @Pattern(regexp = "[0-9]{10,11}", message = "하이픈 없는 10~11자리 숫자를 입력해주세요")
    private String phone;

    @ApiModelProperty(example = "박스")
    @NotBlank(message = "친구의 이름을 입력해주세요")
    private String name;

    @ApiModelProperty(example = "M")
    @Enum(enumClass = Gender.class, message = "친구의 성별 입력이 올바르지 않습니다. M 또는 W가 필요합니다")
    private Gender gender;

    @ApiModelProperty(example = "25")
    @Min(value = 25, message = "25-33세까지만 추천 및 가입 가능합니다")
    @Max(value = 33, message = "25-33세까지만 추천 및 가입 가능합니다")
    private int age;

    @ApiModelProperty(example = "CMC 에서")
    @NotBlank(message = "만나게 된 계기를 입력해주세요")
    private String meet;

    @ApiModelProperty(example = "최고")
    @NotBlank(message = "친구의 성격 키워드를 입력해주세요")
    private String personality;

    @ApiModelProperty(example = "짱")
    @NotBlank(message = "친구의 매력을 입력해주세요")
    private String appeal;

    @ApiModelProperty(example = "1년")
    @NotBlank(message = "만난 기간을 입력해주세요")
    private String period;
//
//    public Member toSender(String phone){
//        Member.MemberBuilder builder = Member.builder();
//        if (this.job != null) {
//            builder.jobName(this.job.getJobName())
//                    .jobPart(this.job.getJobPart())
//                    .jobLocation(this.job.getJobLocation())
//                    .jobPicture(this.job.getJobPicture());
//        }
//
//        if (this.edu != null) {
//            builder.eduName(this.edu.getEduName())
//                    .eduMajor(this.edu.getEduMajor())
//                    .eduLevel(this.edu.getEduMajor())
//                    .eduPicture(this.edu.getEduPicture());
//        }
//
//        return builder
//                .phone(phone)
//                .name(this.name)
//                .gender(this.gender)
//                .age(this.age)
//                .acceptsService(this.acceptsService)
//                .acceptsInfo(this.acceptsInfo)
//                .acceptsReligion(this.acceptsReligion)
//                .acceptsLocation(this.acceptsLocation)
//                .acceptsMarketing(this.acceptsMarketing)
//                .build();
//    }

    public Member toReceiver(String phone){
//        if (job != null) {
//            builder.jobName(this.job.getJobName())
//                    .jobPart(this.job.getJobPart())
//                    .jobLocation(this.job.getJobLocation())
//                    .jobPicture(this.job.getJobPicture());
//        }
//
//        if (edu != null) {
//            builder.eduName(this.edu.getEduName())
//                    .eduMajor(this.edu.getEduMajor())
//                    .eduLevel(this.edu.getEduMajor())
//                    .eduPicture(this.edu.getEduPicture());
//        }

        return Member.builder()
                .phone(phone)
                .name(this.name)
                .gender(this.gender)
                .age(this.age)
                .build();
    }
}