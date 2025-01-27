package com.example.url_shortner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
  @Mock
  UrlRepository urlRepository;

  @Captor
  ArgumentCaptor<Url> urlCaptor;

  UrlService urlService;

  @BeforeEach
  void setup() {
    this.urlService = new UrlService(this.urlRepository);
  }

  @AfterEach
  void purge() {
    this.urlService = null;
  }

  @Test
  void shouldReturn_urlDto_whenShortenUrl_forNewUrl() {
    final Url url = new Url(101L, "a4cec1j0", "http://something.com", Instant.now());
    final UrlDto urldto = new UrlDto("http://something.com");


    when(urlRepository.findByUrl(urldto.url())).thenReturn(Optional.empty());
    when(urlRepository.save(any(Url.class))).thenReturn(url);

    final UrlDto shortUrl = urlService.shortUrl(urldto);

    verify(urlRepository, times(1)).findByUrl(urldto.url());
    verify(urlRepository, times(1)).save(urlCaptor.capture());

    Url savedUrl = urlCaptor.getValue();

    Assertions.assertThat(savedUrl.getUrl().equals(url.getUrl())).isTrue();
    Assertions.assertThat(shortUrl).isNotNull();
    Assertions.assertThat(shortUrl.url().equals(String.format("http://localhost:8080/url-shortner/%s", url.getUuid())));
  }

  @Test
  void shouldReturn_urlDto_whenShortenUrl_forDuplicateUrl() {
    final Url url = new Url(101L, "a4cec1j0", "http://something.com", Instant.now());
    final UrlDto urldto = new UrlDto("http://something.com");

    when(urlRepository.findByUrl(urldto.url())).thenReturn(Optional.of(url));

    final UrlDto shortUrl = urlService.shortUrl(urldto);

    verify(urlRepository, times(1)).findByUrl(urldto.url());
    verify(urlRepository, times(0)).save(any(Url.class));

    Assertions.assertThat(shortUrl).isNotNull();
    Assertions.assertThat(shortUrl.url().equals(String.format("http://localhost:8080/url-shortner/%s", url.getUuid())));
  }

  @Test
  void shouldReturn_originalUrl_forValidUUID() {
    final Url url = new Url(101L, "a4cec1j0", "http://something.com", Instant.now());
    final String uuid = "a4cec1j0";

    when(urlRepository.findByUuid(uuid)).thenReturn(Optional.of(url));

    final String originalUrl = urlService.getUrl(uuid);

    verify(urlRepository, times(1)).findByUuid(uuid);

    Assertions.assertThat(originalUrl).isNotNull();
    Assertions.assertThat(originalUrl.equals("http://something.com"));
  }

  @Test
  void shouldReturn_nullUrl_forInvalidUUID() {
    final String uuid = "a4ceww3a";

    when(urlRepository.findByUuid(uuid)).thenReturn(Optional.empty());

    final String originalUrl = urlService.getUrl(uuid);

    verify(urlRepository, times(1)).findByUuid(uuid);

    Assertions.assertThat(originalUrl).isNull();
  }
}
