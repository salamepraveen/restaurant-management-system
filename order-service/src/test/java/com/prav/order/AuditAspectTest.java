package com.prav.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prav.order.aspect.AuditAspect;
import com.prav.order.annotation.Auditable;

@ExtendWith(MockitoExtension.class)
public class AuditAspectTest {

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private Signature signature;

    @Mock
    private Auditable auditable;

    @InjectMocks
    private AuditAspect auditAspect;

    @BeforeEach
    void setUp() {
        when(joinPoint.getSignature()).thenReturn(signature);
        when(auditable.action()).thenReturn("PLACE_ORDER");
    }

    @Test
    void audit_success() throws Throwable {
        when(signature.getName()).thenReturn("placeOrder");
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L, 100L});
        Object expectedResult = new Object();
        when(joinPoint.proceed()).thenReturn(expectedResult);

        Object result = auditAspect.audit(joinPoint, auditable);

        assertEquals(expectedResult, result);
        verify(auditable).action();
        verify(joinPoint).proceed();
    }

    @Test
    void audit_exception_rethrows() throws Throwable {
        when(signature.getName()).thenReturn("updateStatus");
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L, "CONFIRMED"});
        when(joinPoint.proceed()).thenThrow(new RuntimeException("Order not found"));

        assertThrows(RuntimeException.class, () -> auditAspect.audit(joinPoint, auditable));

        verify(joinPoint).proceed();
    }

    @Test
    void audit_logsCorrectAction() throws Throwable {
        when(signature.getName()).thenReturn("updateStatus");
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L});
        when(auditable.action()).thenReturn("UPDATE_ORDER_STATUS");
        when(joinPoint.proceed()).thenReturn(new Object());

        Object result = auditAspect.audit(joinPoint, auditable);

        assertNotNull(result);
        verify(auditable).action();
        verify(joinPoint).getArgs();
    }
}