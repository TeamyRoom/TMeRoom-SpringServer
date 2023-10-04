package org.finalproject.tmeroom.member.repository;

import org.finalproject.tmeroom.member.data.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {

    boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);

    Page<Member> findAllByIdContaining(String id, Pageable pageable);

    Page<Member> findAllByEmailContaining(String email, Pageable pageable);
}
