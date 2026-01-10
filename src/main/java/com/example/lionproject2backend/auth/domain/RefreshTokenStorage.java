package com.example.lionproject2backend.auth.domain;

import com.example.lionproject2backend.global.domain.BaseEntity;
import com.example.lionproject2backend.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "refresh_token_storage")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshTokenStorage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "refresh_token")
    private String refreshToken;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public static RefreshTokenStorage create(User user, String token) {
        RefreshTokenStorage refreshTokenStorage = new RefreshTokenStorage();
        refreshTokenStorage.user = user;
        refreshTokenStorage.refreshToken = token;
        return refreshTokenStorage;
    }

    public void update(String token) {
        this.refreshToken = token;
    }
}
