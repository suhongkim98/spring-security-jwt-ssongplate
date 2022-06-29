package com.example.security.jwt.auth.repository;

import com.example.security.jwt.auth.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
