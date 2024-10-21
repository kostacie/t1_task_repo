package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.kafka.KafkaClientProducer;
import ru.t1.java.demo.mapper.TimeLimitExceedLogMapper;
import ru.t1.java.demo.model.TimeLimitExceedLog;
import ru.t1.java.demo.model.dto.TimeLimitExceedLogDto;
import ru.t1.java.demo.service.TimeLimitExceedLogService;


@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class TimeLimitExceedLogAspect {
    private final TimeLimitExceedLogService timeLimitExceedLogService;
    private final KafkaClientProducer kafkaClientProducer;

    @Value("${track.time-limit-exceed}")
    private long timeLimitExceed;
    @Value("${t1.kafka.topic.client_metric_trace}")
    private String clientMetricTrace;

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
                        .methodSignature(joinPoint.getSignature().toString())
                        .executionTime(executionTime).build();

                log.info("Execution time for {}: {} ms", joinPoint.getSignature().toString(), executionTime);

                TimeLimitExceedLogDto dto = TimeLimitExceedLogMapper.toDto(exceedLog);
                kafkaClientProducer.sendTo(clientMetricTrace, dto);
            }
        }
        return result;
    }
}
