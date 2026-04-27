package com.prav.common.exception;
 
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
 
class ExceptionTest {
 
    @Test
    void testExceptions() {
        assertNotNull(new BadRequestException("m"));
        assertNotNull(new ConflictException("m"));
        assertNotNull(new ForbiddenException("m"));
        assertNotNull(new ResourceNotFoundException("r", "f", "v"));
        assertNotNull(new ServiceException("m"));
        assertNotNull(new ServiceException("m", new RuntimeException()));
        assertNotNull(new UnauthorizedException("m"));
    }
}
