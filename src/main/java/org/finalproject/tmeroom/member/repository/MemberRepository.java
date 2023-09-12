package org.finalproject.tmeroom.member.repository;

import org.finalproject.tmeroom.member.data.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {
}
