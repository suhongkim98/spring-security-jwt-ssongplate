package com.example.security.jwt.common.repository;

import com.example.security.jwt.common.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
