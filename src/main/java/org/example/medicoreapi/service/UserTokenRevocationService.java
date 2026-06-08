package org.example.medicoreapi.service;

public interface UserTokenRevocationService {
    void revokeAllUserTokens(Long userId);
}
