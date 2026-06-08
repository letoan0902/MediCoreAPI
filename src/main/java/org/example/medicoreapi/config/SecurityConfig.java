package org.example.medicoreapi.config;

/**
 * ===================================================================
 * CONFIG: SecurityConfig (Cấu hình Spring Security)
 * NGƯỜI LÀM: Người 2 - Lê Tiến Đức (Security + User/Admin)
 * ===================================================================
 *
 * HƯỚNG DẪN:
 * - @Configuration, @EnableWebSecurity, @EnableMethodSecurity
 * - Inject JwtAuthenticationFilter, UserService (UserDetailsService)
 *
 * CÁC BEAN CẦN KHAI BÁO:
 *
 * 1. SecurityFilterChain filterChain(HttpSecurity http)
 *    - Disable CSRF (vì dùng JWT, không dùng session)
 *    - Session: STATELESS
 *    - Cấu hình authorizeHttpRequests:
 *      + permitAll: /api/auth/login, /api/auth/refresh
 *      + Còn lại: authenticated
 *    - Thêm JwtAuthenticationFilter trước UsernamePasswordAuthenticationFilter
 *    - Xử lý exception: authenticationEntryPoint trả 401
 *
 * 2. PasswordEncoder passwordEncoder()
 *    - return new BCryptPasswordEncoder();
 *
 * 3. AuthenticationManager authenticationManager(AuthenticationConfiguration config)
 *    - return config.getAuthenticationManager();
 *
 * 4. AuthenticationProvider authenticationProvider()
 *    - DaoAuthenticationProvider, set UserDetailsService và PasswordEncoder
 *
 * LƯU Ý:
 * - Phối hợp Người 1 (Anh) về JwtAuthenticationFilter
 * - @EnableMethodSecurity để @PreAuthorize hoạt động
 */
