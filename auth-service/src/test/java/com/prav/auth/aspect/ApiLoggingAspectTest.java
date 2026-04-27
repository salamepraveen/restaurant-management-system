package com.prav.auth.aspect;
 
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
 
import java.util.Collections;
import java.util.Enumeration;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
 
class ApiLoggingAspectTest {
 
    private ApiLoggingAspect aspect;
    private AutoCloseable closeable;
 
    @Mock
    private ProceedingJoinPoint joinPoint;
    @Mock
    private Signature signature;
    @Mock
    private HttpServletRequest request;
    @Mock
    private ServletRequestAttributes attributes;
 
    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        aspect = new ApiLoggingAspect();
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getTarget()).thenReturn(new Object());
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
    }
 
    @AfterEach
    void tearDown() throws Exception {
        RequestContextHolder.resetRequestAttributes();
        closeable.close();
    }
 
    @Test
    void testLogController_Success() throws Throwable {
        RequestContextHolder.setRequestAttributes(attributes);
        when(attributes.getRequest()).thenReturn(request);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        
        Enumeration<String> headers = Collections.enumeration(Collections.singletonList("authorization"));
        when(request.getHeaderNames()).thenReturn(headers);
        when(request.getHeader("authorization")).thenReturn("Bearer 12345678901234567890");
 
        when(joinPoint.proceed()).thenReturn("Success Result");
 
        Object result = aspect.logController(joinPoint);
        assertEquals("Success Result", result);
    }
 
    @Test
    void testLogController_Error() throws Throwable {
        RequestContextHolder.setRequestAttributes(attributes);
        when(attributes.getRequest()).thenReturn(request);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/test");
        when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
 
        when(joinPoint.proceed()).thenThrow(new RuntimeException("Test Exception"));
 
        assertThrows(RuntimeException.class, () -> aspect.logController(joinPoint));
    }
 
    @Test
    void testLogService_Success() throws Throwable {
        when(joinPoint.proceed()).thenReturn("Service Success");
        Object result = aspect.logService(joinPoint);
        assertEquals("Service Success", result);
    }
 
    @Test
    void testLogService_Error() throws Throwable {
        when(joinPoint.proceed()).thenThrow(new RuntimeException("Service Error"));
        assertThrows(RuntimeException.class, () -> aspect.logService(joinPoint));
    }
 
    @Test
    void testLogRepository_Fast() throws Throwable {
        when(joinPoint.proceed()).thenReturn("Repo Success");
        Object result = aspect.logRepository(joinPoint);
        assertEquals("Repo Success", result);
    }
 
    @Test
    @SuppressWarnings("java:S2925")
    void testLogRepository_Slow() throws Throwable {
        when(joinPoint.proceed()).thenAnswer(invocation -> {
            Thread.sleep(250);
            return "Slow Success";
        });
        Object result = aspect.logRepository(joinPoint);
        assertEquals("Slow Success", result);
    }
}
