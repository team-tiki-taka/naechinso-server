package com.tikitaka.naechinso.domain.member;

import com.tikitaka.naechinso.domain.member.constant.Gender;
import com.tikitaka.naechinso.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByPhone(String phone);

//    Optional<Member> findTopByIdNotInAndGenderNotAndDetailNotNull(Collection<Long> ids, Gender gender);
    List<Member> findByIdNotInAndGenderNotAndDetailNotNull(Collection<Long> ids, Gender gender);

    /** (임시) 성별에 관계없이 리스트에 없는 멤버 id 리스트를 가져온다 */
    List<Member> findByIdNotInAndDetailNotNull(Collection<Long> ids);

    @Query("SELECT m FROM Member m WHERE m = :member")
    Optional<Member> findByMember(Member member);


    Optional<Member> findTopByGenderNot(Gender gender);

}
