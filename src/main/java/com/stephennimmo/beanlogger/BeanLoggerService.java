package com.stephennimmo.beanlogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class BeanLoggerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanLoggerService.class);
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture scheduledFuture;

    public void start() {
        if (scheduledFuture == null) {
            scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
                LOGGER.info("{}", System.currentTimeMillis());
            }, 1, 1, TimeUnit.SECONDS);
        }
    }

    public void stop() {
        if (scheduledFuture != null) {
            this.scheduledFuture.cancel(true);
            this.scheduledFuture = null;
        }
    }

}
