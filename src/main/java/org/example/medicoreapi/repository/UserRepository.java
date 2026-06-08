package org.example.medicoreapi.repository;

/**
 * ===================================================================
 * REPOSITORY: UserRepository
 * NGƯỜI LÀM: Người 2 - Lê Tiến Đức (Security + User/Admin)
 * ===================================================================
 *
 * HƯỚNG DẪN:
 * - Extends JpaRepository<User, Long>
 * - Thêm các query method cần thiết:
 *   + Optional<User> findByUsername(String username)
 *   + boolean existsByUsername(String username)
 * - Người 1 (Anh) cũng sẽ dùng repo này trong AuthService
 */
