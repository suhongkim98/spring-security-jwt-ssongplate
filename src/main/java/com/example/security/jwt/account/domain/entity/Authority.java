package com.example.security.jwt.account.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "authority")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Authority {

    @Id
    @Column(name = "authority_name", length = 50)
    private String authorityName;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @Builder
    public Authority(String authorityName) {
        this.authorityName = authorityName;
    }
}
