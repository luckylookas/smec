package com.smec.accountexercise;

import com.smec.accountexercise.event.EventDto;
import com.smec.accountexercise.statistics.StatisticsResult;
import com.smec.accountexercise.statistics.StatisticService;
import org.influxdb.InfluxDB;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.InfluxDBContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
public class StatisticServiceIT {

    @Container
    public static InfluxDBContainer influx = new InfluxDBContainer(DockerImageName.parse("influxdb:1.8.3"))
            .withDatabase("events")
            .withUsername("influxuser")
            .withPassword("password");

    @Container
    public static MySQLContainer mysql = new MySQLContainer(DockerImageName.parse("mysql:8"))
            .withUsername("username")
            .withPassword("password")
            .withDatabaseName("smec");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("INFLUX_HOST", influx::getUrl);
        registry.add("flyway.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("flyway.url", mysql::getJdbcUrl);
    }

    @Autowired
    StatisticService statisticService;

    @Autowired
    InfluxDB influxDB;

    @Test
    public void test() {
        statisticService.save(EventDto.builder().id("1").happenedAt(LocalDateTime.of(2015, 11, 20, 10, 0, 0)).type("created").build(), "alice");

        statisticService.save(EventDto.builder().id("a").happenedAt(LocalDateTime.of(2020, 11, 21, 12, 0, 0)).type("updated").build(), "bob");
        statisticService.save(EventDto.builder().id("b").happenedAt(LocalDateTime.of(2020, 11, 21, 13, 0, 0)).type("updated").build(), "bob");
        statisticService.save(EventDto.builder().id("c").happenedAt(LocalDateTime.of(2020, 11, 21, 14, 0, 0)).type("updated").build(), "bob");

        statisticService.save(EventDto.builder().id("d").happenedAt(LocalDateTime.of(2020, 11, 20, 11, 0, 0)).type("created").build(), "bob");
        statisticService.save(EventDto.builder().id("e").happenedAt(LocalDateTime.of(2020, 11, 20, 12, 0, 0)).type("updated").build(), "bob");
        statisticService.save(EventDto.builder().id("f").happenedAt(LocalDateTime.of(2020, 11, 20, 13, 0, 0)).type("updated").build(), "bob");

      //  var bob = statisticService.getStatisticsForAccount("bob");
        var bob = statisticService.getStatisticsForAccount("bob");

        //assertThat(bob).hasSize(3);

        assertThat(bob.stream()
                .filter(it -> "updated".equals(it.getEvent()))
                .filter(it -> LocalDate.of(2020, 11, 20).equals(it.getDay())).findFirst().get())
                .extracting(StatisticsResult::getCount)
                .isEqualTo(2);
        assertThat(bob.stream()
                .filter(it -> "updated".equals(it.getEvent()))
                .filter(it -> LocalDate.of(2020, 11
                        , 21).equals(it.getDay())).findFirst().get())
                .extracting(StatisticsResult::getCount)
                .isEqualTo(3);
        assertThat(bob.stream()
                .filter(it -> "created".equals(it.getEvent())).findFirst().get())
                .extracting(StatisticsResult::getCount)
                .isEqualTo(1);

    }
}
