package com.example.security.jwt.account.domain;

import com.example.security.jwt.account.domain.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
