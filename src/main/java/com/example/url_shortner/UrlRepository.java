package com.example.url_shortner;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlRepository extends JpaRepository<Url, Long> {
  public Optional<Url> findByUuid(String uuid);
  public Optional<Url> findByUrl(String url);
}
