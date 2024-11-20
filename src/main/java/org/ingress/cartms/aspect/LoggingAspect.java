package org.ingress.cartms.aspect;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import org.springframework.stereotype.Component;

import java.util.Arrays;


@Component
@Aspect
@Slf4j
public class LoggingAspect {

    @Pointcut("within(@org.ingress.cartms.annotation.Log *)")
    public void loggingPointCut() {
    }

    @SneakyThrows
    @Around(value = "loggingPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        var methodName = joinPoint.getSignature().getName();
        var className = joinPoint.getTarget().getClass().getSimpleName();
        var args = joinPoint.getArgs();
        logEvent("start", methodName, className, Arrays.toString(args));
        Object response;

        try {
            response = joinPoint.proceed();
        } catch (Throwable throwable) {
            logEvent("error", methodName, className, Arrays.toString(args));
            throw throwable;
        }
        logEvent("end", methodName, className, Arrays.toString(args));
        return response;
    }

    private void logEvent(String eventName, String methodName, String className, String parameters) {
        log.info("ActionLog.{}.{} {} {}", methodName, className, eventName, parameters);
    }

}