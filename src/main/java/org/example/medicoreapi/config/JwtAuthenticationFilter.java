package org.example.medicoreapi.config;

/**
 * ===================================================================
 * FILTER: JwtAuthenticationFilter (Lọc và xác thực JWT mỗi request)
 * NGƯỜI LÀM: Người 1 - Phạm Phương Anh (Auth + JWT)
 * ===================================================================
 *
 * HƯỚNG DẪN:
 * - Extends OncePerRequestFilter
 * - @Component
 * - Inject JwtService, UserDetailsService, TokenBlacklistRepository
 *
 * LOGIC TRONG doFilterInternal():
 * 1. Lấy header "Authorization" từ request
 * 2. Nếu không có hoặc không bắt đầu bằng "Bearer " -> bỏ qua, gọi filterChain.doFilter()
 * 3. Extract token (bỏ "Bearer ")
 * 4. Kiểm tra token có trong blacklist không -> nếu có, trả 401
 * 5. Extract username từ token bằng JwtService
 * 6. Load UserDetails từ UserDetailsService
 * 7. Validate token (chữ ký, hết hạn, username khớp)
 * 8. Nếu hợp lệ -> tạo UsernamePasswordAuthenticationToken, set vào SecurityContextHolder
 * 9. Gọi filterChain.doFilter()
 *
 * LƯU Ý:
 * - Bắt TokenExpiredException -> để GlobalExceptionHandler xử lý trả 401
 * - LOG: logger.warn() khi token bị blacklist hoặc hết hạn
 */
