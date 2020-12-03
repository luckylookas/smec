package com.smec.accountexercise;

import com.smec.accountexercise.account.AccountDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class AccountControllerIT {

    @LocalServerPort
    private int port;

    @Container
    public static MySQLContainer mysql = new MySQLContainer(DockerImageName.parse("mysql:8"))
            .withUsername("user")
            .withPassword("password")
            .withDatabaseName("smec");

    @DynamicPropertySource
    static void mysqlProperties(DynamicPropertyRegistry registry) {
        registry.add("flyway.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
    }

    private RestTemplate restTemplate = new RestTemplateBuilder().build();

    @Test
    public void postAndGet_ById_returnsEvent() {
        var createdAccount = restTemplate.postForEntity("http://localhost:" + port + "/account/", AccountDto.builder().name("user1").build(), AccountDto.class);
        assertThat(createdAccount).extracting(HttpEntity::getBody)
                .matches(account -> "user1".equals(account.getName()))
                .matches(account -> Objects.nonNull(account.getId()));

        var retrievedAccount = restTemplate.getForEntity("http://localhost:" + port + "/account/" + createdAccount.getBody().getId(), AccountDto.class);
        assertThat(retrievedAccount).extracting(HttpEntity::getBody)
                .matches(account -> "user1".equals(account.getName()))
                .matches(account -> createdAccount.getBody().getId().equals(account.getId()));

        restTemplate.put("http://localhost:" + port + "/account/" + retrievedAccount.getBody().getId(), retrievedAccount.getBody().toBuilder().name("newName").build());
        var updatedAccount = restTemplate.getForEntity("http://localhost:" + port + "/account/" + retrievedAccount.getBody().getId(), AccountDto.class);
        assertThat(updatedAccount).extracting(HttpEntity::getBody)
                .matches(account -> "newName".equals(account.getName()))
                .matches(account -> retrievedAccount.getBody().getId().equals(account.getId()));

        restTemplate.delete("http://localhost:" + port + "/account/" + retrievedAccount.getBody().getId());
        assertThrows(HttpClientErrorException.NotFound.class, () -> restTemplate.getForEntity("http://localhost:" + port + "/account/" + retrievedAccount.getBody().getId(), AccountDto.class));
    }
}
