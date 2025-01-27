package com.example.url_shortner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UrlControllerTest {
  @Autowired
  private MockMvc mockMvc;
  
  @MockBean
  private UrlService urlService;
  
  @Test
  void home_shouldReturnHomePage() throws Exception {
    mockMvc.perform(get("/url-shortner"))
      .andExpect(status().isOk())
      .andExpect(view().name("home"))
      .andExpect(model().attributeExists("url"))
      .andExpect(model().attributeExists("shortUrl"));
  }
  
  @Test
  void shortUrl_shouldShortenUrlAndReturnHomePage() throws Exception {
    UrlDto request = new UrlDto("https://www.google.com");
    UrlDto shortUrl = new UrlDto("http://short.url/test");
    
    when(urlService.shortUrl(any(UrlDto.class))).thenReturn(shortUrl);

    mockMvc.perform(post("/url-shortner")
      .contentType(MediaType.APPLICATION_FORM_URLENCODED)
      .param("url", request.url()))
      .andExpect(status().isOk())
      .andExpect(view().name("home"))
      .andExpect(model().attribute("shortUrl", shortUrl))
      .andExpect(model().attributeExists("url"));
  }
  
  @Test
  void shortUrl_shouldReturnErrorPageWhenServiceReturnsNull() throws Exception {
    UrlDto request = new UrlDto("invalid-url");
    when(urlService.shortUrl(any(UrlDto.class))).thenReturn(null);
    
    mockMvc.perform(post("/url-shortner")
      .contentType(MediaType.APPLICATION_FORM_URLENCODED)
      .param("url", request.url()))
      .andExpect(status().isOk())
      .andExpect(view().name("error"));
  }


  @Test
  void redirect_shouldRedirectToOriginalUrl() throws Exception {
    String uuid = "test-uuid";
    String originalUrl = "https://www.google.com";
    
    when(urlService.getUrl(uuid)).thenReturn(originalUrl);
    mockMvc.perform(get("/url-shortner/" + uuid))
      .andExpect(status().is3xxRedirection())
      .andExpect(redirectedUrl(originalUrl));
  }

  @Test
  void redirect_shouldReturnErrorPageWhenUrlNotFound() throws Exception {
    String uuid = "non-existent-uuid";
    when(urlService.getUrl(uuid)).thenReturn(null);
    
    mockMvc.perform(get("/url-shortner/" + uuid))
      .andExpect(status().isOk())
      .andExpect(view().name("error"));
  }
}