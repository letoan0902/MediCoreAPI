package org.example.medicoreapi.exception;

/**
 * ===================================================================
 * EXCEPTION: GlobalExceptionHandler (Xử lý ngoại lệ tập trung - AOP)
 * NGƯỜI LÀM: Nhóm trưởng tạo khung, MỖI NGƯỜI bổ sung exception của module mình
 * ===================================================================
 *
 * HƯỚNG DẪN:
 * - @RestControllerAdvice (AOP - bắt exception toàn cục)
 * - Mỗi method dùng @ExceptionHandler để bắt exception cụ thể
 *
 * CÁC EXCEPTION CẦN XỬ LÝ (KHUNG BAN ĐẦU):
 *
 * 1. @ExceptionHandler(ResourceNotFoundException.class)
 *    - Trả 404 NOT_FOUND + ApiResponse(success=false, message=...)
 *
 * 2. @ExceptionHandler(BadRequestException.class)
 *    - Trả 400 BAD_REQUEST
 *
 * 3. @ExceptionHandler(AccessDeniedException.class)
 *    - Trả 403 FORBIDDEN
 *
 * 4. @ExceptionHandler(AuthenticationException.class)
 *    - Trả 401 UNAUTHORIZED
 *
 * 5. @ExceptionHandler(TokenExpiredException.class) - Người 1 bổ sung
 *    - Trả 401 UNAUTHORIZED + message gợi ý dùng refresh token
 *
 * 6. @ExceptionHandler(MethodArgumentNotValidException.class)
 *    - Trả 400 + danh sách lỗi validation
 *
 * 7. @ExceptionHandler(Exception.class) - fallback
 *    - Trả 500 INTERNAL_SERVER_ERROR
 *    - LOG: logger.error() ghi chi tiết lỗi
 *
 * FORMAT TRẢ VỀ THỐNG NHẤT:
 *   { "success": false, "message": "...", "data": null, "timestamp": "..." }
 */
