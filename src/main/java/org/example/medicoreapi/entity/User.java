package org.example.medicoreapi.entity;

/**
 * ===================================================================
 * ENTITY: User (Bảng tài khoản đăng nhập)
 * NGƯỜI LÀM: Người 2 - Lê Tiến Đức (Security + User/Admin)
 * ===================================================================
 *
 * HƯỚNG DẪN:
 * - Đây là entity chung cho tất cả tài khoản đăng nhập (Admin, Doctor, Patient)
 * - Sử dụng @Entity, @Table(name = "users")
 * - Implement UserDetails của Spring Security
 *
 * CÁC TRƯỜNG CẦN CÓ:
 * - id (Long, @GeneratedValue)
 * - username (String, unique, not null)
 * - password (String, not null) - lưu dạng BCrypt
 * - role (Enum: ADMIN, DOCTOR, PATIENT)
 * - enabled (boolean) - để khóa/mở tài khoản
 * - createdAt, updatedAt (LocalDateTime)
 *
 * QUAN HỆ:
 * - @OneToOne với Doctor (nếu role=DOCTOR)
 * - @OneToOne với Patient (nếu role=PATIENT)
 *
 * LƯU Ý:
 * - Tạo enum Role trong package enums
 * - Override các method của UserDetails (getAuthorities, isAccountNonLocked...)
 * - Dùng @Enumerated(EnumType.STRING) cho role
 * - Dùng Lombok: @Data, @Builder, @NoArgsConstructor, @AllArgsConstructor
 */
