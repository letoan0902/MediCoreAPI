package org.example.medicoreapi.service;

/**
 * ===================================================================
 * SERVICE: JwtService (Xử lý logic JWT token)
 * NGƯỜI LÀM: Người 1 - Phạm Phương Anh (Auth + JWT)
 * ===================================================================
 *
 * HƯỚNG DẪN:
 * - @Service class chứa toàn bộ logic tạo và xác thực JWT
 * - Inject @Value("${jwt.secret}"), @Value("${jwt.access-token-expiration}"),
 *   @Value("${jwt.refresh-token-expiration}")
 *
 * CÁC METHOD CẦN TRIỂN KHAI:
 * - String generateAccessToken(UserDetails userDetails) - tạo access token
 * - String generateRefreshToken(UserDetails userDetails) - tạo refresh token
 * - String extractUsername(String token) - lấy username từ token
 * - boolean isTokenValid(String token, UserDetails userDetails) - kiểm tra token hợp lệ
 * - boolean isTokenExpired(String token) - kiểm tra token hết hạn
 * - Claims extractAllClaims(String token) - private, parse token
 *
 * THƯ VIỆN DÙNG:
 * - io.jsonwebtoken.Jwts, Keys, Claims
 * - Đặt role vào claims: .claim("role", user.getRole().name())
 *
 * LOG: Dùng SLF4J logger.info() khi tạo token thành công
 */
