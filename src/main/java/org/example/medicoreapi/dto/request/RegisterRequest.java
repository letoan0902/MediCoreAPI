package org.example.medicoreapi.dto.request;

/**
 * ===================================================================
 * DTO: RegisterRequest
 * NGƯỜI LÀM: Người 2 - Lê Tiến Đức (Security + User/Admin)
 * ===================================================================
 *
 * HƯỚNG DẪN:
 * - DTO nhận dữ liệu tạo tài khoản mới
 *
 * CÁC TRƯỜNG:
 * - username (String, @NotBlank)
 * - password (String, @NotBlank, @Size(min=6))
 * - role (String hoặc Role enum)
 * - fullName (String)
 * - Các trường bổ sung tùy role (phone, email, specialization...)
 */
