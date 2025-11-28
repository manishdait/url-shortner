package com.example.url_shortner;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
  Optional<Url> findByUrl(String url);
  Optional<Url> findByUuid(String uuid);
}
