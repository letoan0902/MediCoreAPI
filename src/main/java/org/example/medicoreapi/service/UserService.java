package org.example.medicoreapi.service;

import org.example.medicoreapi.dto.request.RegisterRequest;
import org.example.medicoreapi.dto.response.UserResponse;
import org.example.medicoreapi.entity.Doctor;
import org.example.medicoreapi.entity.Patient;
import org.example.medicoreapi.entity.User;
import org.example.medicoreapi.enums.Role;
import org.example.medicoreapi.exception.BadRequestException;
import org.example.medicoreapi.exception.ResourceNotFoundException;
import org.example.medicoreapi.repository.DoctorRepository;
import org.example.medicoreapi.repository.PatientRepository;
import org.example.medicoreapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional
public class UserService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        Role role = request.getRole() == null ? Role.PATIENT : request.getRole();
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .enabled(request.getEnabled() == null || request.getEnabled())
                .build();
        User saved = userRepository.save(user);
        createProfileIfRequested(saved, request);

        logger.info("Created user {} with role {}", saved.getUsername(), saved.getRole());
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return toDto(findUser(id));
    }

    public UserResponse updateUser(Long id, RegisterRequest request) {
        User user = findUser(id);
        if (StringUtils.hasText(request.getUsername()) && !user.getUsername().equals(request.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new BadRequestException("Username already exists");
            }
            user.setUsername(request.getUsername());
        }
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }

        User saved = userRepository.save(user);
        logger.info("Updated user {}", saved.getUsername());
        return toDto(saved);
    }

    public void deleteUser(Long id) {
        User user = findUser(id);
        doctorRepository.findByUserId(id).ifPresent(doctorRepository::delete);
        patientRepository.findByUserId(id).ifPresent(patientRepository::delete);
        userRepository.delete(user);
        logger.info("Deleted user {}", user.getUsername());
    }

    public UserResponse lockAccount(Long userId) {
        User user = findUser(userId);
        user.setEnabled(false);
        user.revokeIssuedTokens();
        User saved = userRepository.save(user);
        logger.info("Locked user {} and invalidated issued tokens", saved.getUsername());
        return toDto(saved);
    }

    public UserResponse unlockAccount(Long userId) {
        User user = findUser(userId);
        user.setEnabled(true);
        User saved = userRepository.save(user);
        logger.info("Unlocked user {}", saved.getUsername());
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Transactional(readOnly = true)
    public User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void createProfileIfRequested(User user, RegisterRequest request) {
        if (!StringUtils.hasText(request.getFullName())) {
            return;
        }

        if (user.getRole() == Role.DOCTOR && !doctorRepository.existsByUserId(user.getId())) {
            Doctor doctor = Doctor.builder()
                    .fullName(request.getFullName())
                    .specialization(request.getSpecialization())
                    .phone(request.getPhone())
                    .email(request.getEmail())
                    .user(user)
                    .build();
            Doctor savedDoctor = doctorRepository.save(doctor);
            user.setDoctor(savedDoctor);
        }

        if (user.getRole() == Role.PATIENT && !patientRepository.existsByUserId(user.getId())) {
            Patient patient = Patient.builder()
                    .fullName(request.getFullName())
                    .dateOfBirth(request.getDateOfBirth())
                    .gender(request.getGender())
                    .phone(request.getPhone())
                    .address(request.getAddress())
                    .user(user)
                    .build();
            Patient savedPatient = patientRepository.save(patient);
            user.setPatient(savedPatient);
        }
    }

    private UserResponse toDto(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        response.setEnabled(user.isEnabled());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        doctorRepository.findByUserId(user.getId()).ifPresent(doctor -> response.setDoctorId(doctor.getId()));
        patientRepository.findByUserId(user.getId()).ifPresent(patient -> response.setPatientId(patient.getId()));
        return response;
    }
}
