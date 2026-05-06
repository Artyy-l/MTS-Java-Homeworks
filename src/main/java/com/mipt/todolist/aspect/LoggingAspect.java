package com.mipt.todolist.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.mipt.todolist.service..*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        log.debug("Service method started: {}", methodName);
        try {
            Object result = joinPoint.proceed();
            log.debug("Service method finished: {}, result={}", methodName, describeResult(result));
            return result;
        } catch (Throwable throwable) {
            log.info("Service method failed: {}, error={}", methodName, throwable.getMessage());
            throw throwable;
        }
    }

    private String describeResult(Object result) {
        if (result == null) {
            return "void";
        }
        if (result instanceof Collection<?> collection) {
            return result.getClass().getSimpleName() + "(size=" + collection.size() + ")";
        }
        return result.getClass().getSimpleName();
    }
}
