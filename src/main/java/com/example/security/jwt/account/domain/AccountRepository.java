package com.example.security.jwt.account.domain;

import com.example.security.jwt.account.domain.entity.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long>, AccountCustomRepository {
    @EntityGraph(attributePaths = "authorities") // 엔티티그래프 통해 EAGER로 가져온다.
    Optional<Account> findOneWithAuthoritiesByUsername(String username); // user를 기준으로 유저를 조회할 때 권한정보도 가져온다.
}
