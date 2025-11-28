package com.example.url_shortner;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Optional;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
  private static final String CACHE_NAME = "urls";
  
  public final UrlRepository urlRepository;

  @Cacheable(cacheNames = CACHE_NAME, key = "#requestUrl")
  @RateLimiter(name = "shortenUrlRateLimiter", fallbackMethod = "rateLimiterFallback")
  public Url shortenUrl(String requestUrl) {
    Optional<Url> _url = urlRepository.findByUrl(requestUrl);
    
    if (_url.isPresent()) {
      return _url.get();
    }

    Url url = urlRepository.save(Url.builder()
      .url(requestUrl)
      .uuid(generateUUID())
      .createdAt(LocalDate.now())
      .build()
    );

    return cacheUrl(url);
  }

  @Cacheable(cacheNames = CACHE_NAME, key = "{#uuid}")
  public Url redirectUrl(String uuid) {
    return urlRepository.findByUuid(uuid).orElseThrow(
      () -> new RuntimeException("Invalid Url...")
    );
  }

  private String generateUUID() {
    int nano = Instant.now().getNano();
    return Base64.getEncoder().encodeToString(
      String.valueOf(nano).getBytes()
    );
  }

  @Caching(
    put = {
      @CachePut(cacheNames = CACHE_NAME, key = "#result.uuid"),
      @CachePut(cacheNames = CACHE_NAME, key = "#result.url")
    }
  )
  public Url cacheUrl(Url url) {
    return url;
  }

  Url rateLimiterFallback(String requestUrl, Throwable t) {
    log.warn("RateLimiter fired");
    throw new RuntimeException("Too many request...");
  }
}
