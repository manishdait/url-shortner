package com.example.url_shortner;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
@Entity
@Table(name = "url")
public class Url implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "url_seq_generator")
  @SequenceGenerator(name = "url_seq_generator", sequenceName = "url_seq", initialValue = 101, allocationSize = 1)
  @Column(name = "id")
  private Long id;

  @Column(name = "url", nullable = false, unique = true)
  private String url;

  @Column(name = "uuid", nullable = false, unique = true)
  private String uuid;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDate createdAt;
}
