package com.smec.accountexercise.statistics;

import com.smec.accountexercise.config.InfluxProperties;
import lombok.RequiredArgsConstructor;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.influxdb.querybuilder.BuiltQuery.QueryBuilder.*;

@Component
@RequiredArgsConstructor
public class InfluxClient {

    public static final String EVENT = "event";
    public static final String COUNT = "count";
    public static final int COUNT_INDEX = 1;
    public static final int DATE_INDEX = 0;
    public static final String DAY_GROUP_DENOMINATOR = "d";
    private static final DateTimeFormatter df = DateTimeFormatter.ISO_DATE_TIME;
    private final InfluxProperties properties;
    private final InfluxDB influxDB;

    public void save(String account, String eventType, Long timestampMs) {
        Point point = Point
                .measurement(account)
                .tag(EVENT, eventType)
                .addField(EVENT, eventType) // influx needs at least one field
                .time(timestampMs, TimeUnit.MILLISECONDS)
                .build();
        influxDB.write(point);
    }

    public List<StatisticsResult> getAggregatedEventsForAccount(String account) {
        var query = select().count(EVENT).as(COUNT).from(properties.getDb(), account).groupBy(time(1L, DAY_GROUP_DENOMINATOR), EVENT).orderBy(asc());
        return influxDB.query(query).getResults()
                .stream()
                .map(QueryResult.Result::getSeries)
                .flatMap(Collection::stream)
                .flatMap(eventResult ->
                        eventResult.getValues().stream()
                                .map(values -> StatisticsResult.builder()
                                        .event(eventResult.getTags().get(EVENT))
                                        .count(((Double) values.get(COUNT_INDEX)).intValue())
                                        .day(LocalDate.from(df.parse((String) values.get(DATE_INDEX))))
                                        .build()
                                )
                ).collect(Collectors.toList());
    }
}
