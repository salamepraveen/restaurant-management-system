package com.prav.user.aspect;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import com.prav.user.aspect.ApiLoggingAspect;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

    @InjectMocks
    private ApiLoggingAspect loggingAspect;

    @Mock
    private ProceedingJoinPoint joinPoint;

    private ListAppender<ILoggingEvent> createAppender() {
        Logger logger = (Logger) LoggerFactory.getLogger(ApiLoggingAspect.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        return appender;
    }

    private void mockRequest(String method, String uri) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn(method);
        when(request.getRequestURI()).thenReturn(uri);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    // ========== logController ==========

    @Test
    void logController_successfulExecution_logsStartAndEnd() throws Throwable {
        mockRequest("GET", "/test");

        when(joinPoint.getTarget()).thenReturn(new Object());
        when(joinPoint.getSignature()).thenReturn(mock(org.aspectj.lang.Signature.class));
        when(joinPoint.getSignature().getName()).thenReturn("getUser");
        when(joinPoint.proceed()).thenReturn("result");
        when(joinPoint.getArgs()).thenReturn(new Object[0]);

        ListAppender<ILoggingEvent> appender = createAppender();

        Object result = loggingAspect.logController(joinPoint);

        assertEquals("result", result);
        long controllerLogs = appender.list.stream()
                .filter(e -> e.getFormattedMessage().contains("[CONTROLLER]"))
                .count();
        assertTrue(controllerLogs >= 2, "Should have at least 2 controller logs");
        assertTrue(appender.list.stream().anyMatch(e -> e.getFormattedMessage().contains("RESPONSE")));
        
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void logController_exceptionThrown_logsFailure() throws Throwable {
        mockRequest("DELETE", "/test/1");

        when(joinPoint.getTarget()).thenReturn(new Object());
        when(joinPoint.getSignature()).thenReturn(mock(org.aspectj.lang.Signature.class));
        when(joinPoint.getSignature().getName()).thenReturn("deleteUser");
        when(joinPoint.proceed()).thenThrow(new RuntimeException("Not found"));
        when(joinPoint.getArgs()).thenReturn(new Object[0]);

        ListAppender<ILoggingEvent> appender = createAppender();

        assertThrows(RuntimeException.class, () -> loggingAspect.logController(joinPoint));

        assertTrue(appender.list.stream().anyMatch(e -> e.getFormattedMessage().contains("INCOMING")));
        assertTrue(appender.list.stream().anyMatch(e -> e.getFormattedMessage().contains("ERROR")));
        assertTrue(appender.list.stream().anyMatch(e -> e.getFormattedMessage().contains("Not found")));
        
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void logController_returnsProceedResult() throws Throwable {
        mockRequest("GET", "/all");
        Object fakeResult = new Object();
        when(joinPoint.getTarget()).thenReturn(new Object());
        when(joinPoint.getSignature()).thenReturn(mock(org.aspectj.lang.Signature.class));
        when(joinPoint.getSignature().getName()).thenReturn("getAll");
        when(joinPoint.proceed()).thenReturn(fakeResult);
        when(joinPoint.getArgs()).thenReturn(new Object[0]);

        ListAppender<ILoggingEvent> appender = createAppender();

        Object result = loggingAspect.logController(joinPoint);

        assertSame(fakeResult, result);
    }

    // ========== logService ==========

    @Test
    void logService_successfulExecution_logsStartAndEnd() throws Throwable {
        when(joinPoint.getTarget()).thenReturn(new Object());
        when(joinPoint.getSignature()).thenReturn(mock(org.aspectj.lang.Signature.class));
        when(joinPoint.getSignature().getName()).thenReturn("createUser");
        when(joinPoint.proceed()).thenReturn("result");

        ListAppender<ILoggingEvent> appender = createAppender();

        Object result = loggingAspect.logService(joinPoint);

        assertEquals("result", result);
        long serviceLogs = appender.list.stream()
                .filter(e -> e.getFormattedMessage().contains("[SERVICE]"))
                .count();
        assertEquals(2, serviceLogs); // START + END
        assertTrue(appender.list.stream().anyMatch(e -> e.getFormattedMessage().contains("START")));
        assertTrue(appender.list.stream().anyMatch(e -> e.getFormattedMessage().contains("END")));
    }

    @Test
    void logService_exceptionThrown_logsFailure() throws Throwable {
        when(joinPoint.getTarget()).thenReturn(new Object());
        when(joinPoint.getSignature()).thenReturn(mock(org.aspectj.lang.Signature.class));
        when(joinPoint.getSignature().getName()).thenReturn("promoteUser");
        when(joinPoint.proceed()).thenThrow(new RuntimeException("User not found"));

        ListAppender<ILoggingEvent> appender = createAppender();

        assertThrows(RuntimeException.class, () -> loggingAspect.logService(joinPoint));

        assertTrue(appender.list.stream().anyMatch(e -> e.getFormattedMessage().contains("START")));
        assertTrue(appender.list.stream().anyMatch(e -> e.getFormattedMessage().contains("FAILED")));
        assertTrue(appender.list.stream().anyMatch(e -> e.getFormattedMessage().contains("User not found")));
    }

    @Test
    void logService_returnsProceedResult() throws Throwable {
        Object fakeResult = new Object();
        when(joinPoint.getTarget()).thenReturn(new Object());
        when(joinPoint.getSignature()).thenReturn(mock(org.aspectj.lang.Signature.class));
        when(joinPoint.getSignature().getName()).thenReturn("demoteUser");
        when(joinPoint.proceed()).thenReturn(fakeResult);

        ListAppender<ILoggingEvent> appender = createAppender();

        Object result = loggingAspect.logService(joinPoint);

        assertSame(fakeResult, result);
    }

    @Test
    void loggingAspect_isNotNull() {
        assertNotNull(loggingAspect);
    }
}