package org.example.medicoreapi.service;

import org.example.medicoreapi.dto.request.LoginRequest;
import org.example.medicoreapi.dto.request.RefreshTokenRequest;
import org.example.medicoreapi.dto.response.AuthResponse;
import org.example.medicoreapi.entity.TokenBlacklist;
import org.example.medicoreapi.entity.User;
import org.example.medicoreapi.exception.BadRequestException;
import org.example.medicoreapi.exception.ResourceNotFoundException;
import org.example.medicoreapi.repository.TokenBlacklistRepository;
import org.example.medicoreapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@Transactional
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    public AuthService(
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            TokenBlacklistRepository tokenBlacklistRepository
    ) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.tokenBlacklistRepository = tokenBlacklistRepository;
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        logger.info("User {} logged in successfully", user.getUsername());
        return buildAuthResponse(user, accessToken, refreshToken);
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        if (tokenBlacklistRepository.existsByToken(refreshToken)) {
            logger.error("Rejected blacklisted refresh token");
            throw new BadCredentialsException("Refresh token has been revoked");
        }
        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new BadRequestException("Token must be a refresh token");
        }

        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        String newAccessToken = jwtService.generateAccessToken(user);
        logger.info("Issued new access token for user {}", username);
        return buildAuthResponse(user, newAccessToken, refreshToken);
    }

    public void logout(String accessToken, String refreshToken) {
        revokeToken(accessToken, JwtService.ACCESS_TOKEN_TYPE, "SELF");
        revokeToken(refreshToken, JwtService.REFRESH_TOKEN_TYPE, "SELF");
        logger.info("Logout completed and tokens revoked");
    }

    public void revokeAllUserTokens(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.revokeIssuedTokens();
        userRepository.save(user);
        logger.info("Revoked all issued tokens for user {}", user.getUsername());
    }

    private void revokeToken(String token, String tokenType, String revokedBy) {
        if (!StringUtils.hasText(token) || tokenBlacklistRepository.existsByToken(token)) {
            return;
        }

        String username = null;
        LocalDateTime expiryDate = LocalDateTime.now();
        try {
            username = jwtService.extractUsername(token);
            expiryDate = jwtService.extractExpiryDateTime(token);
        } catch (RuntimeException ex) {
            logger.warn("Revoking token whose claims could not be fully read: {}", ex.getMessage());
        }

        tokenBlacklistRepository.save(TokenBlacklist.builder()
                .token(token)
                .tokenType(tokenType)
                .username(username)
                .expiryDate(expiryDate)
                .revokedAt(LocalDateTime.now())
                .revokedBy(revokedBy)
                .build());
    }

    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getAccessTokenExpirationSeconds())
                .role(user.getRole().name())
                .username(user.getUsername())
                .build();
    }
}
