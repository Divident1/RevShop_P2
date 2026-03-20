package com.revshop.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LoggingAspect.
 * Verifies the AOP aspect correctly logs and delegates method calls.
 */
@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

    @InjectMocks
    private LoggingAspect loggingAspect;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private Signature signature;

    @BeforeEach
    void setUp() {
        // No additional setup required — mocks are initialized by Mockito
    }

    // --- @Around — Service Layer Tests ---

    @Test
    @DisplayName("logAroundService - should proceed and return result")
    void logAroundService_Success() throws Throwable {
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("getAllProducts");
        when(proceedingJoinPoint.getTarget()).thenReturn(new Object());
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{});
        when(proceedingJoinPoint.proceed()).thenReturn("success");

        Object result = loggingAspect.logAroundService(proceedingJoinPoint);

        assertEquals("success", result);
        verify(proceedingJoinPoint, times(1)).proceed();
    }

    @Test
    @DisplayName("logAroundService - should re-throw exception after logging")
    void logAroundService_Exception() throws Throwable {
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("addProduct");
        when(proceedingJoinPoint.getTarget()).thenReturn(new Object());
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{"arg1"});
        when(proceedingJoinPoint.proceed()).thenThrow(new RuntimeException("Test error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> loggingAspect.logAroundService(proceedingJoinPoint));

        assertEquals("Test error", ex.getMessage());
    }

    @Test
    @DisplayName("logAroundService - should handle methods with multiple args")
    void logAroundService_MultipleArgs() throws Throwable {
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("updateProduct");
        when(proceedingJoinPoint.getTarget()).thenReturn(new Object());
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{1L, "data"});
        when(proceedingJoinPoint.proceed()).thenReturn("updated");

        Object result = loggingAspect.logAroundService(proceedingJoinPoint);

        assertEquals("updated", result);
    }

    // --- @Before & @AfterReturning — Controller Layer Tests ---

    @Test
    @DisplayName("logBeforeController - should execute without error")
    void logBeforeController_Success() {
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("login");
        when(joinPoint.getTarget()).thenReturn(new Object());
        when(joinPoint.getArgs()).thenReturn(new Object[]{"request"});

        assertDoesNotThrow(() -> loggingAspect.logBeforeController(joinPoint));
    }

    @Test
    @DisplayName("logAfterController - should execute without error")
    void logAfterController_Success() {
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("login");
        when(joinPoint.getTarget()).thenReturn(new Object());

        assertDoesNotThrow(() -> loggingAspect.logAfterController(joinPoint, "response"));
    }

    // --- @AfterThrowing — Exception Logging Tests ---

    @Test
    @DisplayName("logControllerException - should log exception without error")
    void logControllerException_Success() {
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("placeOrder");
        when(joinPoint.getTarget()).thenReturn(new Object());

        RuntimeException ex = new RuntimeException("Controller error");

        assertDoesNotThrow(
                () -> loggingAspect.logControllerException(joinPoint, ex));
    }

    @Test
    @DisplayName("logRepositoryException - should log DB exception without error")
    void logRepositoryException_Success() {
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("findById");
        when(joinPoint.getTarget()).thenReturn(new Object());

        RuntimeException ex = new RuntimeException("DB error");

        assertDoesNotThrow(
                () -> loggingAspect.logRepositoryException(joinPoint, ex));
    }
}
