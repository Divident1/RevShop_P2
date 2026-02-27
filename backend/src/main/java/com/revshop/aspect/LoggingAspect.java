package com.revshop.aspect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * ═══════════════════════════════════════════════════════════════════════
 * LoggingAspect — Cross-cutting concern for application-wide logging
 * ═══════════════════════════════════════════════════════════════════════
 *
 * Uses Spring AOP to automatically log:
 * • Method entry (with arguments)
 * • Method exit (with return value)
 * • Execution time
 * • Exceptions thrown
 *
 * This keeps business logic in services clean and free of logging code.
 * Follows the Separation of Concerns (SoC) principle.
 */
@Aspect
@Component
public class LoggingAspect {

    // ══════════════════════════════════════════════════════════════════
    // Pointcut Definitions
    // ══════════════════════════════════════════════════════════════════

    /** Matches all methods in the service.impl package */
    @Pointcut("execution(* com.revshop.service.impl.*.*(..))")
    public void serviceLayer() {
    }

    /** Matches all methods in the controller package */
    @Pointcut("execution(* com.revshop.controller.*.*(..))")
    public void controllerLayer() {
    }

    /** Matches all methods in the repository package */
    @Pointcut("execution(* com.revshop.repository.*.*(..))")
    public void repositoryLayer() {
    }

    // ══════════════════════════════════════════════════════════════════
    // Service Layer — @Around for entry + exit + execution time
    // ══════════════════════════════════════════════════════════════════

    /**
     * Logs method entry, exit, return value, and execution time
     * for all service-layer methods.
     */
    @Around("serviceLayer()")
    public Object logAroundService(ProceedingJoinPoint joinPoint) throws Throwable {

        Logger logger = LogManager.getLogger(joinPoint.getTarget().getClass());

        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();

        // ── Entry ──
        logger.info("➡  ENTER  {}.{}() | Args: {}",
                className, methodName, Arrays.toString(args));

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - startTime;

            // ── Exit ──
            logger.info("✅  EXIT   {}.{}() | Time: {}ms | Return: {}",
                    className, methodName, elapsed, result);

            return result;

        } catch (Exception ex) {
            long elapsed = System.currentTimeMillis() - startTime;

            // ── Exception ──
            logger.error("❌  ERROR  {}.{}() | Time: {}ms | Exception: {} - {}",
                    className, methodName, elapsed,
                    ex.getClass().getSimpleName(), ex.getMessage());

            throw ex; // re-throw so normal flow continues
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // Controller Layer — @Before and @AfterReturning
    // ══════════════════════════════════════════════════════════════════

    /**
     * Logs every incoming request hitting a controller method.
     */
    @Before("controllerLayer()")
    public void logBeforeController(JoinPoint joinPoint) {

        Logger logger = LogManager.getLogger(joinPoint.getTarget().getClass());

        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        logger.info("🌐  REQUEST  {}.{}() | Args: {}",
                className, methodName, Arrays.toString(joinPoint.getArgs()));
    }

    /**
     * Logs successful controller responses.
     */
    @AfterReturning(pointcut = "controllerLayer()", returning = "result")
    public void logAfterController(JoinPoint joinPoint, Object result) {

        Logger logger = LogManager.getLogger(joinPoint.getTarget().getClass());

        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        logger.info("🌐  RESPONSE {}.{}() | Result: {}",
                className, methodName, result);
    }

    /**
     * Logs controller-level exceptions.
     */
    @AfterThrowing(pointcut = "controllerLayer()", throwing = "ex")
    public void logControllerException(JoinPoint joinPoint, Throwable ex) {

        Logger logger = LogManager.getLogger(joinPoint.getTarget().getClass());

        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        logger.error("🌐  EXCEPTION {}.{}() | {} : {}",
                className, methodName,
                ex.getClass().getSimpleName(), ex.getMessage());
    }

    // ══════════════════════════════════════════════════════════════════
    // Repository Layer — @AfterThrowing only (keep it lightweight)
    // ══════════════════════════════════════════════════════════════════

    /**
     * Logs any exception thrown from a repository (database) call.
     */
    @AfterThrowing(pointcut = "repositoryLayer()", throwing = "ex")
    public void logRepositoryException(JoinPoint joinPoint, Throwable ex) {

        Logger logger = LogManager.getLogger(joinPoint.getTarget().getClass());

        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        logger.error("💾  DB ERROR  {}.{}() | {} : {}",
                className, methodName,
                ex.getClass().getSimpleName(), ex.getMessage());
    }
}
