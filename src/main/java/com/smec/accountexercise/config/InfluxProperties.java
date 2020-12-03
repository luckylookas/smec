package com.smec.accountexercise.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InfluxProperties {
    @Value("${INFLUX_URI}")
    private String uri;
    @Value("${INFLUX_USER}")
    private String user;
    @Value("${INFLUX_PASSWORD}")
    private String password;
    @Value("${INFLUX_DB}")
    private String db;
}
