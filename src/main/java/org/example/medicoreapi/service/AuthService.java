package org.example.medicoreapi.service;

/**
 * ===================================================================
 * SERVICE: AuthService (Xử lý nghiệp vụ đăng nhập / đăng xuất / refresh)
 * NGƯỜI LÀM: Người 1 - Phạm Phương Anh (Auth + JWT)
 * ===================================================================
 *
 * HƯỚNG DẪN:
 * - @Service, inject JwtService, AuthenticationManager, UserRepository,
 *   TokenBlacklistRepository, PasswordEncoder
 *
 * CÁC METHOD CẦN TRIỂN KHAI:
 * - AuthResponse login(LoginRequest request)
 *   + Authenticate bằng AuthenticationManager
 *   + Tạo access + refresh token, trả về AuthResponse
 *
 * - AuthResponse refreshToken(RefreshTokenRequest request)
 *   + Kiểm tra refresh token có trong blacklist không
 *   + Validate refresh token, extract username
 *   + Tạo access token mới, trả về AuthResponse
 *
 * - void logout(String accessToken, String refreshToken)
 *   + Đưa cả access và refresh token vào blacklist
 *
 * - void revokeAllUserTokens(Long userId)
 *   + Admin gọi để vô hiệu hóa tất cả token của 1 user
 *   + Phối hợp với Người 2 (Đức) để kết nối từ AdminController
 *
 * LOG: logger.info() khi login/logout/revoke thành công
 *      logger.error() khi có lỗi xác thực
 */
