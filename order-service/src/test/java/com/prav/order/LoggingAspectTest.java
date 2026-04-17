package com.prav.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prav.order.aspect.ApiLoggingAspect;

@ExtendWith(MockitoExtension.class)
public class LoggingAspectTest {

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private Signature signature;

    @InjectMocks
    private ApiLoggingAspect loggingAspect;

    @BeforeEach
    void setUp() {
        when(joinPoint.getSignature()).thenReturn(signature);
    }

    @Test
    void logController_success() throws Throwable {
        when(signature.getName()).thenReturn("getMyOrders");
        when(joinPoint.getTarget()).thenReturn(new Object());
        Object expectedResult = List.of();
        when(joinPoint.proceed()).thenReturn(expectedResult);

        Object result = loggingAspect.logController(joinPoint);

        assertEquals(expectedResult, result);
        verify(joinPoint).proceed();
    }

    @Test
    void logController_exception_rethrows() throws Throwable {
        when(signature.getName()).thenReturn("placeOrder");
        when(joinPoint.getTarget()).thenReturn(new Object());
        when(joinPoint.proceed()).thenThrow(new RuntimeException("Pizza not found"));

        assertThrows(RuntimeException.class, () -> loggingAspect.logController(joinPoint));

        verify(joinPoint).proceed();
    }

    @Test
    void logService_success() throws Throwable {
        when(signature.getName()).thenReturn("placeOrder");
        when(joinPoint.getTarget()).thenReturn(new Object());
        Object expectedResult = new Object();
        when(joinPoint.proceed()).thenReturn(expectedResult);

        Object result = loggingAspect.logService(joinPoint);

        assertEquals(expectedResult, result);
        verify(joinPoint).proceed();
    }

    @Test
    void logService_exception_rethrows() throws Throwable {
        when(signature.getName()).thenReturn("placeOrder");
        when(joinPoint.getTarget()).thenReturn(new Object());
        when(joinPoint.proceed()).thenThrow(new RuntimeException("DB down"));

        assertThrows(RuntimeException.class, () -> loggingAspect.logService(joinPoint));

        verify(joinPoint).proceed();
    }
}