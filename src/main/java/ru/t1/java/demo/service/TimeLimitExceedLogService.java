package ru.t1.java.demo.service;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import ru.t1.java.demo.model.TimeLimitExceedLog;

public interface TimeLimitExceedLogService {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    void save(TimeLimitExceedLog exceedLog);

}
