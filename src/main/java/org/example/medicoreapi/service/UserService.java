package org.example.medicoreapi.service;

/**
 * ===================================================================
 * SERVICE: UserService (Quản lý tài khoản - CRUD + khóa/mở)
 * NGƯỜI LÀM: Người 2 - Lê Tiến Đức (Security + User/Admin)
 * ===================================================================
 *
 * HƯỚNG DẪN:
 * - @Service, inject UserRepository, PasswordEncoder, AuthService (để revoke token)
 *
 * CÁC METHOD CẦN TRIỂN KHAI:
 * - User createUser(RegisterRequest request) - Admin tạo tài khoản
 *   + Mã hóa password bằng BCryptPasswordEncoder
 *   + Gán role, lưu vào DB
 *
 * - List<User> getAllUsers() - lấy danh sách (dùng Stream API map sang DTO)
 * - User getUserById(Long id)
 * - User updateUser(Long id, RegisterRequest request)
 * - void deleteUser(Long id)
 *
 * - void lockAccount(Long userId) - Admin khóa tài khoản
 *   + Set enabled = false
 *   + Gọi AuthService.revokeAllUserTokens(userId) để thu hồi token
 *
 * - void unlockAccount(Long userId) - Admin mở khóa
 *
 * - UserDetails loadUserByUsername(String username)
 *   + Implement UserDetailsService interface
 *   + Trả về User entity (đã implement UserDetails)
 *
 * LOG: logger.info() khi tạo/khóa/mở tài khoản thành công
 */
