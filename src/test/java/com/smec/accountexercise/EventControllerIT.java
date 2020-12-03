package com.smec.accountexercise;

import com.smec.accountexercise.account.AccountService;
import com.smec.accountexercise.event.EventDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.InfluxDBContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Sql({"/sql/eventControllerIT.sql"})
public class EventControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    AccountService accountService;

    @Container
    public static InfluxDBContainer influx = new InfluxDBContainer(DockerImageName.parse("influxdb:1.8.3"))
            .withDatabase("events")
            .withUsername("influxuser")
            .withPassword("password");

    @Container
    public static MySQLContainer mysql = new MySQLContainer(DockerImageName.parse("mysql:8"))
            .withUsername("user")
            .withPassword("password")
            .withDatabaseName("smec");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("INFLUX_HOST", influx::getUrl);
        registry.add("flyway.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("flyway.url", mysql::getJdbcUrl);
    }

    private RestTemplate restTemplate = new RestTemplateBuilder().build();

    @MockBean
    private Clock clock;

    @BeforeEach
    public void setup() {
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        when(clock.instant()).thenReturn(Instant.ofEpochMilli(150_000));
    }

    @Test
    public void postAndGet_ById_returnsEvent() {

        var createdEvent = restTemplate.postForEntity("http://localhost:" + port + "/event/account/user-1-id",
                EventDto.builder().type("created").happenedAt(LocalDateTime.now(clock)).build(), EventDto.class);

        var retrievedEvent = restTemplate.getForEntity("http://localhost:" + port + "/event/" + createdEvent.getBody().getId(), EventDto.class);
        assertThat(retrievedEvent).extracting(HttpEntity::getBody)
                .matches(event -> LocalDateTime.now(clock).equals(event.getHappenedAt()))
                .matches(event -> "created".equals(event.getType()))
                .matches(event -> createdEvent.getBody().getId().equals(event.getId()));
    }

    @Test
    public void postAndGet_omitHappendAt_assumesNow() {
        var createdEvent = restTemplate.postForEntity("http://localhost:" + port + "/event/account/user-1-id", EventDto.builder().type("created").build(), EventDto.class);
        var retrievedEvent = restTemplate.getForEntity("http://localhost:" + port + "/event/" + createdEvent.getBody().getId(), EventDto.class);
        assertThat(retrievedEvent).extracting(HttpEntity::getBody)
                .matches(event -> LocalDateTime.now(clock).equals(event.getHappenedAt()))
                .matches(event -> "created".equals(event.getType()))
                .matches(event -> createdEvent.getBody().getId().equals(event.getId()));

    }
}
