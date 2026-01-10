package com.example.lionproject2backend.auth.repository;

import com.example.lionproject2backend.auth.domain.RefreshTokenStorage;
import com.example.lionproject2backend.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenStorageRepository extends JpaRepository<RefreshTokenStorage, Long> {
    Optional<RefreshTokenStorage> findByUser(User user);
    Optional<RefreshTokenStorage> findByRefreshToken(String refreshToken);
    void deleteByUser(User user);
}
