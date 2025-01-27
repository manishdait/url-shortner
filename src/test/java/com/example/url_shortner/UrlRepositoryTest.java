package com.example.url_shortner;

import java.time.Instant;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UrlRepositoryTest {
  @Container
  @ServiceConnection
  private static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:alpine"));

  @Autowired
  private UrlRepository urlRepository;

  @BeforeEach
  void setup() {
    Url url = Url.builder()
      .uuid("hash-uuid")
      .url("http://something.com")
      .createdAt(Instant.now())
      .build();

    urlRepository.save(url);
  }

  @AfterEach
  void purge() {
    urlRepository.deleteAll();
  }

  @Test
  void shouldReturn_urlOptional_forValid_uuid() {
    final String uuid = "hash-uuid";
    final Optional<Url> result = urlRepository.findByUuid(uuid);
    Assertions.assertThat(result).isPresent();
  }

  @Test
  void shouldReturn_emptyOptional_forInvalid_uuid() {
    final String uuid = "invalid-uuid";
    final Optional<Url> result = urlRepository.findByUuid(uuid);
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  void shouldReturn_urlOptional_forValid_url() {
    final String url = "http://something.com";
    final Optional<Url> result = urlRepository.findByUrl(url);
    Assertions.assertThat(result).isPresent();
  }

  @Test
  void shouldReturn_emptyOptional_forInvalid_url() {
    final String url = "http://invalid.com";
    final Optional<Url> result = urlRepository.findByUrl(url);
    Assertions.assertThat(result).isEmpty();
  }
}
