package org.example.medicoreapi.repository;

/**
 * ===================================================================
 * REPOSITORY: TokenBlacklistRepository
 * NGƯỜI LÀM: Người 1 - Phạm Phương Anh (Auth + JWT)
 * ===================================================================
 *
 * HƯỚNG DẪN:
 * - Extends JpaRepository<TokenBlacklist, Long>
 * - Query methods:
 *   + boolean existsByToken(String token)
 *   + void deleteByExpiryDateBefore(LocalDateTime dateTime) - dọn token hết hạn
 */
