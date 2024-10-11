package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.TimeLimitExceedLog;
import ru.t1.java.demo.service.TimeLimitExceedLogService;


//3.2 Аспект, логирующий сообщения в TimeLimitExceedLog, если аннотированный аспектом метод работает дольше,
// чем установленное значение в конфигурационном параметре
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class TimeLimitExceedLogAspect {
    @Autowired
    private TimeLimitExceedLogService timeLimitExceedLogService;
    @Value("${track.time-limit-exceed}")
    private long timeLimitExceed;

    @Around("@annotation(ru.t1.java.demo.aop.TrackExecutionTime)")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Method call: {}", joinPoint.getSignature().toShortString());
        long startTime = System.currentTimeMillis();
        Object result;
        try {
            result = joinPoint.proceed();
        } finally {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            if (executionTime > timeLimitExceed) {
                TimeLimitExceedLog exceedLog = TimeLimitExceedLog.builder()
                        .methodSignature(joinPoint.getSignature().toShortString())
                        .executionTime(executionTime).build();
                log.info("Execution time for {}: {} ms", joinPoint.getSignature().toString(), executionTime);
                timeLimitExceedLogService.save(exceedLog);
            }
        }
        return result;
    }
}
