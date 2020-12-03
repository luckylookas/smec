package com.smec.accountexercise.event;

import com.smec.accountexercise.config.TimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class ExpiryHandler {

    private final EventRepository eventRepository;
    private final TimeService timeService;

    /*
     delete all events older than 30 days every day at midnight
     */
    @Scheduled(cron = "0 0 * * *")
    public void expireEvents() {
        eventRepository.deleteAllByHappenedAtBefore(timeService.now().minus(30, ChronoUnit.DAYS));
    }
}
