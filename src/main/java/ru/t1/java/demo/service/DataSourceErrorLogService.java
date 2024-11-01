package ru.t1.java.demo.service;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import ru.t1.java.demo.model.DataSourceErrorLog;

public interface DataSourceErrorLogService {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    void save(DataSourceErrorLog errorLog);
}
