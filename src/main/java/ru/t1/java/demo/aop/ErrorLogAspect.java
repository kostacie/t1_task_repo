package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.service.DataSourceErrorLogService;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ErrorLogAspect {
    private final DataSourceErrorLogService dataSourceErrorLogService;

    @AfterThrowing(value = "execution(public * ru.t1*..*.*(..))", throwing = "ex")
public void addErrorLog(JoinPoint joinPoint, Throwable ex) {
        DataSourceErrorLog errLog = DataSourceErrorLog.builder()
                .stackTrace(Arrays.toString(ex.getStackTrace()))
                .message(ex.getMessage())
                .methodSignature(joinPoint.getSignature().toString())
                .build();

        log.error("Exception in method: {}", joinPoint.getSignature().toShortString());
        dataSourceErrorLogService.save(errLog);
    }
}
