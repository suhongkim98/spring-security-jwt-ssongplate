package com.example.security.jwt.account.repository;

import com.example.security.jwt.account.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
