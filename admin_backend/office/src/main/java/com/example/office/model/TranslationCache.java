package com.example.office.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "translation_cache", uniqueConstraints = {
        @UniqueConstraint(name = "uk_pair", columnNames = {"originalText", "fromLang", "toLang"})
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranslationCache {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String originalText;

    private String fromLang;

    private String toLang;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String translated;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}