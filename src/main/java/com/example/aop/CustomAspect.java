package com.example.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class CustomAspect {

    @Around("Pointcuts.allEndpoints()")
    public Object aroundEndpointAdvice(ProceedingJoinPoint joinPoint) {
        return defaultHandlingForProceedingJoinPoint(joinPoint, "controller", "endpoint-method");
    }

    @Around("Pointcuts.allServiceMethods()")
    public Object aroundServiceMethodAdvice(ProceedingJoinPoint joinPoint) {
        return defaultHandlingForProceedingJoinPoint(joinPoint, "service");
    }

//    @Around("Pointcuts.allModelMethods()") // does not find methods
//    public Object aroundModelMethodAdvice(ProceedingJoinPoint joinPoint) {
//        return defaultHandlingForProceedingJoinPoint(joinPoint, "model");
//    }

    @Around("Pointcuts.allUtilMethods()")
    public Object aroundUtilMethodAdvice(ProceedingJoinPoint joinPoint) {
        return defaultHandlingForProceedingJoinPoint(joinPoint, "util-class");
    }

    private Object defaultHandlingForProceedingJoinPoint(ProceedingJoinPoint joinPoint, String layer) {
        return defaultHandlingForProceedingJoinPoint(joinPoint, layer, "method");
    }

    private Object defaultHandlingForProceedingJoinPoint(ProceedingJoinPoint joinPoint, String layer, String methodDesignation) {

        var signature = (MethodSignature) joinPoint.getSignature();

        String message = String.format("%s '%s', %s '%s'",
                layer,
                signature.getMethod().getDeclaringClass().getName(),
                methodDesignation,
                signature.getName());

        Object result;

        try {
            log.info(message + " is invoked");
            result = joinPoint.proceed();
            log.info(message + " is completed");
        } catch (RuntimeException e) {
            log.error(message + " has thrown an error");
            throw e;
        } catch (Throwable e) {
            log.error(message + " has thrown an error");
            throw new RuntimeException(e);
        }

        return result;
    }
}
