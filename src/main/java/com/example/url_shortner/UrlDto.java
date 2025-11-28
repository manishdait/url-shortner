package com.example.url_shortner;

public record UrlDto(String url) {
    UrlDto() {
      this(null);
    }
  }