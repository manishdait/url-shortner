package com.example.url_shortner;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
  private final UrlRepository urlRepository;

  @Transactional
  public UrlDto shortUrl(UrlDto request) {
    Optional<Url> _url = urlRepository.findByUrl(request.url());

    if (_url.isPresent()) {
      return new UrlDto(String.format("http://localhost:8080/url-shortner/%s", _url.get().getUuid()));
    }

    String uuid;

    do {
      uuid = generateUUID();
      if (uuid == null) {
        log.error("Error processing the request");
        return null;
      }
    } while (urlRepository.findByUuid(uuid).isPresent());

    Url url = Url.builder()
      .uuid(uuid)
      .url(request.url())
      .createdAt(Instant.now())
      .build();
    
    urlRepository.save(url);

    return new UrlDto(String.format("http://localhost:8080/url-shortner/%s", url.getUuid()));
  }

  private String generateUUID() {
    UUID uuid = UUID.randomUUID();
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] hashDigest = md.digest(uuid.toString().getBytes());
      String hash = Base64.getUrlEncoder().withoutPadding().encodeToString(hashDigest);

      return hash.substring(0, Math.min(8, hash.length()));
    } catch (NoSuchAlgorithmException e) {
      log.error("SHA-256 algorithm not found");
      return null;
    }
  }

  @Transactional
  public String getUrl(String uuid) {
    Optional<Url> _url = urlRepository.findByUuid(uuid);
    if (_url.isEmpty()) {
      log.error("Error processing the request");
      return null;
    }
    return _url.get().getUrl();
  }
}
