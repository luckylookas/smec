package com.smec.accountexercise.statistics;

import com.smec.accountexercise.config.TimeService;
import com.smec.accountexercise.event.EventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticService {

    private final InfluxClient influxClient;
    private final TimeService timeService;

    public void save(EventDto eventDto, String account) {
        influxClient.save(account, eventDto.getType(), timeService.millis(eventDto.getHappenedAt()));
    }

    public List<StatisticsResult> getStatisticsForAccount(String account) {
        return influxClient.getAggregatedEventsForAccount(account);
    }
}
