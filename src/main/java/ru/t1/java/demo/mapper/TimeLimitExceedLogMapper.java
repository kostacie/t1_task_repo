package ru.t1.java.demo.mapper;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.TimeLimitExceedLog;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.model.dto.TimeLimitExceedLogDto;

@Component
public class TimeLimitExceedLogMapper {
    public static TimeLimitExceedLog toEntity(TimeLimitExceedLogDto dto) {
        return TimeLimitExceedLog.builder()
                .methodSignature(dto.getMethodSignature())
                .executionTime(dto.getExecutionTime())
                .build();
    }

    public static TimeLimitExceedLogDto toDto(TimeLimitExceedLog entity) {
        return TimeLimitExceedLogDto.builder()
                .methodSignature(entity.getMethodSignature())
                .executionTime(entity.getExecutionTime())
                .build();
    }
}
