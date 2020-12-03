package com.smec.accountexercise.config;

import lombok.RequiredArgsConstructor;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class InfluxConfig {

    private final InfluxProperties properties;

    @Bean
    public InfluxDB influxDB() {
        var db = InfluxDBFactory.connect(properties.getUri(), properties.getUser(), properties.getPassword());
        db.setDatabase(properties.getDb());
        return db;
    }
}
