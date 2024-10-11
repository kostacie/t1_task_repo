package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.model.TimeLimitExceedLog;
import ru.t1.java.demo.repository.TimeLimitExceedLogRepository;
import ru.t1.java.demo.service.TimeLimitExceedLogService;


@Service
@RequiredArgsConstructor
public class TimeLimitExceedLogServiceImpl implements TimeLimitExceedLogService {
    @Autowired
    private TimeLimitExceedLogRepository repository;
    @Override
    public void save(TimeLimitExceedLog exceedLog) {
        repository.save(exceedLog);
    }
}
