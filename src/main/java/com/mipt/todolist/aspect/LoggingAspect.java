package com.mipt.todolist.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Аспект для логирования вызовов методов в слое service: начало, завершение и результат
 */
@Aspect
@Component
public class LoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.mipt.todolist.service..*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        log.info("LoggingAspect: начало {}", methodName);
        Object result;
        try {
            result = joinPoint.proceed();
            if (result != null) {
                log.info("LoggingAspect: конец {} -> результат: {}", methodName, result);
            } else {
                log.info("LoggingAspect: конец {} -> (нет возвращаемого значения)", methodName);
            }
            return result;
        } catch (Throwable t) {
            log.info("LoggingAspect: конец {} -> исключение: {}", methodName, t.getMessage());
            throw t;
        }
    }
}
