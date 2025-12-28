package com.example.office.repository;

import com.example.office.model.TranslationCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TranslationCacheRepository extends JpaRepository<TranslationCache, Long> {
    Optional<TranslationCache> findByOriginalTextAndFromLangAndToLang(String originalText, String fromLang, String toLang);
}