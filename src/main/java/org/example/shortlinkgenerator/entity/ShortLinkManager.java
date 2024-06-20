package org.example.shortlinkgenerator.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "short_url")
public class ShortLinkManager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "short_url", unique = true)
    private String shortUrl;

    @NotNull
    @Column(name = "url", unique = true)
    private String url;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
