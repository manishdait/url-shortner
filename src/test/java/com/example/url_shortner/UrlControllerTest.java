package com.example.url_shortner;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class UrlControllerTest {
  private static final String BASE_SHORT_URL = "http://localhost:8080";
  private static final String FORM_SUBMIT_ENDPOINT = "/shorten-url";
  
  @Autowired
  private MockMvc mockMvc;
  
  @MockitoBean
  private UrlService urlService;
  
  @Test
  void home_shouldReturnHomePage() throws Exception {
    mockMvc.perform(get("/"))
      .andExpect(status().isOk())
      .andExpect(view().name("index"))
      .andExpect(model().attributeExists("url"))
      .andExpect(model().attributeExists("short_url"));
  }
  
  @Test
  void shortUrl_shouldShortenUrlAndReturnHomePage() throws Exception {
    final Url response = createMockUrl();
    final String request = "https://www.large-url.com";
    
    when(urlService.shortenUrl(request)).thenReturn(response);

    mockMvc.perform(post(FORM_SUBMIT_ENDPOINT)
      .contentType(MediaType.APPLICATION_FORM_URLENCODED)
      .param("url", request))
      .andExpect(status().is3xxRedirection())
      .andExpect(view().name("redirect:"))
      .andExpect(flash().attribute(
        "short_url", 
        String.format("%s/%s", BASE_SHORT_URL, response.getUuid())
      ))
      .andExpect(flash().attributeExists("short_url"));
  }

  @Test
  void redirect_shouldRedirectToOriginalUrl() throws Exception {
    final Url response = createMockUrl();
    final String uuid = "uuid";
    
    when(urlService.redirectUrl(uuid)).thenReturn(response);
    mockMvc.perform(get("/" + uuid))
      .andExpect(status().is3xxRedirection())
      .andExpect(redirectedUrl(response.getUrl()));
  }

  @Test
  void redirect_shouldReturnErrorhenUrlNotFound() throws Exception {
    String uuid = "non-existent-uuid";
    when(urlService.redirectUrl(uuid)).thenThrow(new RuntimeException("Invalid url..."));
    
    mockMvc.perform(get("/" + uuid))
      .andExpect(status().isNotFound())
      .andExpect(view().name("error"));
  }

  // Helper

  private Url createMockUrl() {
    return new Url(
      101L,
      "http://www.large-url.com",
      "uuid",
      LocalDate.now()
    );
  }
}