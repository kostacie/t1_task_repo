package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;
import ru.t1.java.demo.service.DataSourceErrorLogService;

@Service
@RequiredArgsConstructor
public class DataSourceErrorLogServiceImpl implements DataSourceErrorLogService {
    @Autowired
    private DataSourceErrorLogRepository repository;
    @Override
    public void save(DataSourceErrorLog errorLog) {
        repository.save(errorLog);
    }
}
