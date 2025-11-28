package com.example.url_shortner;

import java.time.LocalDate;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class UrlRepositoryTest {
  @Container
  @ServiceConnection
  private static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:alpine"));

  @Autowired
  private UrlRepository urlRepository;

  @BeforeEach
  void setup() {
    Url url = Url.builder()
      .url("http://something.com")
      .uuid("uuid")
      .createdAt(LocalDate.now())
      .build();

    urlRepository.save(url);
  }

  @AfterEach
  void purge() {
    urlRepository.deleteAll();
  }

  @Test
  void shouldReturn_urlOptional_forValid_uuid() {
    final String uuid = "uuid";
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
