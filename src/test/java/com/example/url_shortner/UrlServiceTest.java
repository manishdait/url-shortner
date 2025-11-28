package com.example.url_shortner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
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
    final Url mockResponse = Mockito.mock(Url.class);
    final String url = "http://something.com";

    when(urlRepository.findByUrl(url)).thenReturn(Optional.empty());
    when(urlRepository.save(any(Url.class))).thenReturn(mockResponse);

    final Url result = urlService.shortenUrl(url);

    verify(urlRepository, times(1)).findByUrl(url);
    verify(urlRepository, times(1)).save(urlCaptor.capture());
    
    Assertions.assertThat(result).isNotNull();
    
    final Url capture = urlCaptor.getValue();
    
    Assertions.assertThat(capture.getUrl()).isEqualTo(url);
    Assertions.assertThat(capture.getUuid()).isNotNull();
    Assertions.assertThat(capture.getCreatedAt()).isNotNull();
  }

  @Test
  void shouldReturn_urlDto_whenShortenUrl_forDuplicateUrl() {
    final Url mockUrl = Mockito.mock(Url.class);
    final String url = "http://something.com";

    when(urlRepository.findByUrl(url)).thenReturn(Optional.of(mockUrl));

    final Url result = urlService.shortenUrl(url);

    verify(urlRepository, times(1)).findByUrl(url);
    verify(urlRepository, times(0)).save(any(Url.class));

    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result).isEqualTo(mockUrl);
  }

  @Test
  void shouldReturn_originalUrl_forValidUUID() {
    final Url mockUrl = Mockito.mock(Url.class);
    final String uuid = "uuid";

    when(urlRepository.findByUuid(uuid)).thenReturn(Optional.of(mockUrl));

    final Url result = urlService.redirectUrl(uuid);

    verify(urlRepository, times(1)).findByUuid(uuid);

    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result).isEqualTo(mockUrl);
  }

  @Test
  void should_throwExcepiton_forInvalidUUID() {
    final String uuid = "invalid-uuid";

    when(urlRepository.findByUuid(uuid)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> urlService.redirectUrl(uuid))
      .isInstanceOf(RuntimeException.class);
  }
}
