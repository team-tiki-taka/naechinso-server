package com.tikitaka.naechinso.domain.member.entity;

import com.tikitaka.naechinso.global.config.entity.BaseEntity;
import com.tikitaka.naechinso.global.config.entity.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

/**
 * 추천 받은 멤버 가입 정보를 담당하는 엔티티입니다
 * @author gengminy 220924
 * */
@Entity
@Table(name = "member_detail")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"member"})
@EqualsAndHashCode(exclude = {"member"}) //one to one cycle hashcode 방지
public class MemberDetail extends BaseEntity {
    // member_detail_id => mem_id
    @Id
    @Column(name = "mem_id")
    private Long id;

//    //추천해준 사람의 PK
//    @ManyToOne
//    @JoinColumn(name = "mem_id2")
//    private Member recommender;

    @Column(name = "mem_height")
    private Integer height;

    @Column(name = "mem_address")
    private String address;

    @Column(name = "mem_religion")
    private String religion;

    @Column(name = "mem_drink")
    private String drink;

    @Column(name = "mem_smoke")
    private String smoke;

    @Column(name = "mem_mbti")
    @Builder.Default
    private String mbti = "";

    @Column(name = "mem_personality")
    private String personality;

    @Column(name = "mem_introduce")
    @Builder.Default
    private String introduce = "";

    @Column(name = "mem_picture")
    private String picture;

    @Column(name = "mem_point")
    private Long point;

    @Column(name = "mem_school")
    private String school;

    @Column(name = "mem_major")
    private String major;

    @Column(name = "mem_edu_level")
    private String eduLevel;




    // Member Entity 와 1:1 조인
    // Member PK 그대로 사용
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "mem_id")
    private Member member;
}