package org.example.medicoreapi.dto.response;

/**
 * ===================================================================
 * DTO: AuthResponse (Phản hồi đăng nhập / refresh token)
 * NGƯỜI LÀM: Người 1 - Phạm Phương Anh (Auth + JWT)
 * ===================================================================
 *
 * HƯỚNG DẪN:
 * - DTO trả về sau khi login hoặc refresh token thành công
 *
 * CÁC TRƯỜNG:
 * - accessToken (String)
 * - refreshToken (String)
 * - tokenType (String) - mặc định "Bearer"
 * - expiresIn (Long) - thời gian sống access token (giây)
 * - role (String) - vai trò user
 *
 * LƯU Ý: Dùng @Builder pattern cho dễ khởi tạo
 */
