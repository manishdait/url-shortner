package com.example.url_shortner;

import jakarta.validation.constraints.NotNull;

public record UrlDto(@NotNull(message = "url must not be null") String url) {

}
