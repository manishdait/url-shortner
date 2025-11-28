package com.example.url_shortner;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class UrlController { 
  private final UrlService urlService;

  @GetMapping()
  public String home(Model model) {
    model.addAttribute("message", "Hello");
    model.addAttribute("url", new UrlDto(""));

    if (!model.containsAttribute("short_url")) {
      model.addAttribute("short_url", "");
    }

    return "index";
  }

  @PostMapping("/shorten-url")
  public String shortenUrl(UrlDto request, RedirectAttributes redirectAttributes) {
    Url url = urlService.shortenUrl(request.url());
    String shortUrl = "http://localhost:8080/%s".formatted(url.getUuid());

    redirectAttributes.addFlashAttribute("short_url", shortUrl);
    return "redirect:";
  }

  @GetMapping("/{uuid}")
  public String getUrl(@PathVariable String uuid, HttpServletResponse response) {
    try {
      Url url = urlService.redirectUrl(uuid);
      return "redirect:" + url.getUrl();
    } catch (Exception e) {
      response.setStatus(404);
      return "error";
    }
  }
}
