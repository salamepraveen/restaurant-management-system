package com.prav.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prav.order.aspect.PerformanceAspect;

@ExtendWith(MockitoExtension.class)
public class PerformanceAspectTest {

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private Signature signature;

    @InjectMocks
    private PerformanceAspect performanceAspect;

    @BeforeEach
    void setUp() {
        lenient().when(joinPoint.getSignature()).thenReturn(signature);
    }

    @Test
    void fastQuery_returnsResult() throws Throwable {
        lenient().when(signature.getName()).thenReturn("findByUserId");
        lenient().when(joinPoint.getTarget()).thenReturn(new Object());
        List<Object> expectedResult = new ArrayList<>();
        when(joinPoint.proceed()).thenReturn(expectedResult);

        Object result = performanceAspect.monitorDatabaseQueries(joinPoint);

        assertEquals(expectedResult, result);
        verify(joinPoint).proceed();
    }

    @Test
    void slowQuery_logsWarning() throws Throwable {
        lenient().when(signature.getName()).thenReturn("findByRestaurantId");
        lenient().when(joinPoint.getTarget()).thenReturn(new Object());

        when(joinPoint.proceed()).thenAnswer(invocation -> {
            Thread.sleep(600);
            return List.of();
        });

        Object result = performanceAspect.monitorDatabaseQueries(joinPoint);

        assertNotNull(result);
        verify(joinPoint).proceed();
    }

    @Test
    void queryThrowsException_rethrows() throws Throwable {
        lenient().when(signature.getName()).thenReturn("findById");
        lenient().when(joinPoint.getTarget()).thenReturn(new Object());
        when(joinPoint.proceed()).thenThrow(new RuntimeException("Connection timeout"));

        assertThrows(RuntimeException.class, () -> performanceAspect.monitorDatabaseQueries(joinPoint));

        verify(joinPoint).proceed();
    }
}