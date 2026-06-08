package org.example.medicoreapi.service;

import org.example.medicoreapi.dto.request.RegisterRequest;
import org.example.medicoreapi.entity.User;
import org.example.medicoreapi.enums.Role;
import org.example.medicoreapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ObjectProvider<UserTokenRevocationService> tokenRevocationProvider;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder, tokenRevocationProvider);
    }

    @Test
    void createUser_Success_EncodesPasswordAndEnablesAccount() {
        RegisterRequest request = new RegisterRequest("doctor01", "secret123", Role.DOCTOR, "Doctor One");
        when(userRepository.existsByUsername("doctor01")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = userService.createUser(request);

        assertThat(created.getUsername()).isEqualTo("doctor01");
        assertThat(created.getPassword()).isEqualTo("encoded-password");
        assertThat(created.getRole()).isEqualTo(Role.DOCTOR);
        assertThat(created.isEnabled()).isTrue();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_DuplicateUsername_ThrowsException() {
        RegisterRequest request = new RegisterRequest("admin", "secret123", Role.ADMIN, null);
        when(userRepository.existsByUsername("admin")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username already exists");

        verify(userRepository, never()).save(any());
    }

    @Test
    void getAllUsers_ReturnsList() {
        User admin = new User("admin", "encoded", Role.ADMIN, true);
        User patient = new User("patient01", "encoded", Role.PATIENT, true);
        when(userRepository.findAll()).thenReturn(List.of(admin, patient));

        assertThat(userService.getAllUsers()).containsExactly(admin, patient);
    }

    @Test
    void lockAccount_Success_DisablesAccountAndRevokesTokens() {
        User user = userWithId(10L, "patient01", Role.PATIENT, true);
        UserTokenRevocationService revocationService = mock(UserTokenRevocationService.class);
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        doAnswer(invocation -> {
            Consumer<UserTokenRevocationService> consumer = invocation.getArgument(0);
            consumer.accept(revocationService);
            return null;
        }).when(tokenRevocationProvider).ifAvailable(any());

        userService.lockAccount(10L);

        assertThat(user.isEnabled()).isFalse();
        verify(userRepository).save(user);
        verify(revocationService).revokeAllUserTokens(10L);
    }

    @Test
    void unlockAccount_Success_EnablesAccount() {
        User user = userWithId(11L, "patient02", Role.PATIENT, false);
        when(userRepository.findById(11L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.unlockAccount(11L);

        assertThat(user.isEnabled()).isTrue();
        verify(userRepository).save(user);
    }

    @Test
    void deleteUser_NotFound_ThrowsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");

        verify(userRepository, never()).delete(any());
    }

    @Test
    void loadUserByUsername_Success_ReturnsSpringSecurityUser() {
        User user = userWithId(1L, "admin", Role.ADMIN, true);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername("admin");

        assertThat(userDetails.getUsername()).isEqualTo("admin");
        assertThat(userDetails.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    void loadUserByUsername_NotFound_ThrowsException() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("missing"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    private static User userWithId(Long id, String username, Role role, boolean enabled) {
        User user = new User(username, "encoded", role, enabled);
        user.setId(id);
        return user;
    }
}
