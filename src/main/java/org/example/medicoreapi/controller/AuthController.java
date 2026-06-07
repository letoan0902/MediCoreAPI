package org.example.medicoreapi.controller;

/**
 * ===================================================================
 * CONTROLLER: AuthController (API xác thực: login, refresh, logout)
 * NGƯỜI LÀM: Người 1 - Phạm Phương Anh (Auth + JWT)
 * ===================================================================
 *
 * HƯỚNG DẪN:
 * - @RestController, @RequestMapping("/api/auth")
 * - Inject AuthService
 *
 * CÁC ENDPOINT:
 *
 * POST /api/auth/login
 * - Nhận: @RequestBody @Valid LoginRequest
 * - Trả: ResponseEntity<ApiResponse<AuthResponse>> (200 OK)
 *
 * POST /api/auth/refresh
 * - Nhận: @RequestBody @Valid RefreshTokenRequest
 * - Trả: ResponseEntity<ApiResponse<AuthResponse>> (200 OK)
 * - Logic: gửi refresh token, nhận access token mới
 *
 * POST /api/auth/logout
 * - Nhận: Access token từ Header "Authorization: Bearer xxx"
 *         Refresh token từ @RequestBody
 * - Trả: ResponseEntity<ApiResponse<Void>> (200 OK)
 * - Logic: đưa cả 2 token vào blacklist
 *
 * LƯU Ý:
 * - Endpoint login và refresh KHÔNG cần authentication (permitAll)
 * - Endpoint logout CẦN authentication
 * - Phối hợp Người 2 (Đức) để cấu hình permitAll trong SecurityConfig
 */
