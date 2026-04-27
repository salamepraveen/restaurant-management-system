package com.prav.common.dto;
 
import org.junit.jupiter.api.Test;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
 
class CommonDTOTest {
 
    @Test
    void testApiResponse() {
        ApiResponse<String> res = ApiResponse.<String>builder()
                .success(true)
                .message("m")
                .data("d")
                .build();
        assertTrue(res.isSuccess());
        assertEquals("m", res.getMessage());
        assertEquals("d", res.getData());
    }
 
    @Test
    void testErrorResponse() {
        ErrorResponse res = ErrorResponse.builder()
                .status(400)
                .message("m")
                .timestamp(java.time.LocalDateTime.now())
                .fieldErrors(Collections.singletonList("d"))
                .path("/p")
                .build();
        assertEquals(400, res.getStatus());
        assertEquals("m", res.getMessage());
        assertEquals("/p", res.getPath());
        assertNotNull(res.getFieldErrors());
    }
 
    @Test
    void testPagedResponse() {
        PagedResponse<String> res = PagedResponse.<String>builder()
                .content(Collections.singletonList("d"))
                .pageNumber(1)
                .pageSize(10)
                .totalElements(100L)
                .totalPages(10)
                .last(true)
                .build();
        assertEquals(1, res.getPageNumber());
        assertEquals(10, res.getPageSize());
        assertEquals(100L, res.getTotalElements());
        assertTrue(res.isLast());
    }
}
