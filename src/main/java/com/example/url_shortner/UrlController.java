package com.example.url_shortner;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequestMapping("/url-shortner")
@RequiredArgsConstructor
public class UrlController {
  private final UrlService urlService;
  
  @GetMapping
  public String home(Model model) {
    model.addAttribute("url", new UrlDto(null));
    model.addAttribute("shortUrl", new UrlDto(null));
    return "home";
  }
  

  @PostMapping
  public String shortUrl(@ModelAttribute("url") UrlDto request, Model model) {
    UrlDto shortUrl = urlService.shortUrl(request);
    if (shortUrl == null) {
      return "error";
    }

    model.addAttribute("url", new UrlDto(null));
    model.addAttribute("shortUrl", shortUrl);
    return "home";
  }

  @GetMapping("/{uuid}")
  public String redirect(@PathVariable String uuid) {
    String url = urlService.getUrl(uuid);
    if (url == null) {
      return "error";
    }
    return "redirect:" + url;
  }
}
