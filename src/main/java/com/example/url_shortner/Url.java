package com.example.url_shortner;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "url")
public class Url {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "url_sequence_generator")
  @SequenceGenerator(name = "url_sequence_generator", sequenceName = "url_sequence", initialValue = 101, allocationSize = 1)
  private Long id;

  @Column(name = "uuid", unique = true)
  @NotBlank(message = "uuid must not be blank")
  private String uuid;

  @Column(name = "url", columnDefinition = "TEXT")
  @NotBlank(message = "url must not be blank")
  private String url;

  @Column(name = "created_at", updatable = false)
  @NotNull(message = "created_at must not be null")
  private Instant createdAt;
}
