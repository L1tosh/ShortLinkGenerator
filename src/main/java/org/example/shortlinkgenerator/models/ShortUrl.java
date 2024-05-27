package org.example.shortlinkgenerator.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "short_url")
public class ShortUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "short_url", unique = true)
    private String shortUrl;

    @NotNull
    @Column(name = "url")
    private String url;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
