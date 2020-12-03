package com.smec.accountexercise.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TimeService {

    private final Clock clock;

    public long millis(LocalDateTime time) {
        return time.toInstant(clock.getZone().getRules().getOffset(time)).toEpochMilli();
    }

    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

}
