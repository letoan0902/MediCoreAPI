package org.example.medicoreapi.service;

import org.example.medicoreapi.dto.request.LoginRequest;
import org.example.medicoreapi.dto.request.RefreshTokenRequest;
import org.example.medicoreapi.dto.response.AuthResponse;
import org.example.medicoreapi.entity.TokenBlacklist;
import org.example.medicoreapi.entity.User;
import org.example.medicoreapi.enums.Role;
import org.example.medicoreapi.repository.TokenBlacklistRepository;
import org.example.medicoreapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private TokenBlacklistRepository tokenBlacklistRepository;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        authenticationManager = mock(AuthenticationManager.class);
        userRepository = mock(UserRepository.class);
        tokenBlacklistRepository = mock(TokenBlacklistRepository.class);
        authService = new AuthService(jwtService, authenticationManager, userRepository, tokenBlacklistRepository);
    }

    @Test
    void login_shouldReturnAccessAndRefreshTokens() {
        LoginRequest request = new LoginRequest();
        request.setUsername("doctor1");
        request.setPassword("secret123");
        User user = User.builder().id(1L).username("doctor1").password("hash").role(Role.DOCTOR).enabled(true).build();

        when(userRepository.findByUsername("doctor1")).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token");
        when(jwtService.getAccessTokenExpirationSeconds()).thenReturn(1800L);

        AuthResponse response = authService.login(request);

        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("DOCTOR", response.getRole());
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void refreshToken_blacklistedToken_shouldThrowUnauthorized() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("revoked-refresh");
        when(tokenBlacklistRepository.existsByToken("revoked-refresh")).thenReturn(true);

        assertThrows(BadCredentialsException.class, () -> authService.refreshToken(request));
    }

    @Test
    void logout_shouldBlacklistBothTokens() {
        when(tokenBlacklistRepository.existsByToken("access")).thenReturn(false);
        when(tokenBlacklistRepository.existsByToken("refresh")).thenReturn(false);
        when(jwtService.extractUsername("access")).thenReturn("patient1");
        when(jwtService.extractUsername("refresh")).thenReturn("patient1");
        when(jwtService.extractExpiryDateTime("access")).thenReturn(LocalDateTime.now().plusMinutes(30));
        when(jwtService.extractExpiryDateTime("refresh")).thenReturn(LocalDateTime.now().plusDays(7));

        authService.logout("access", "refresh");

        ArgumentCaptor<TokenBlacklist> captor = ArgumentCaptor.forClass(TokenBlacklist.class);
        verify(tokenBlacklistRepository, times(2)).save(captor.capture());
        assertEquals("ACCESS", captor.getAllValues().get(0).getTokenType());
        assertEquals("REFRESH", captor.getAllValues().get(1).getTokenType());
    }
}
