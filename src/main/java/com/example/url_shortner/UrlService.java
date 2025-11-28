package com.example.url_shortner;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Optional;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UrlService {
  private static final String CACHE_NAME = "urls";
  
  public final UrlRepository urlRepository;

  @CachePut(cacheNames = CACHE_NAME, key = "{#result.uuid}")
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

    return url;
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
}
