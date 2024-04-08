package com.goorm.wordsketch.repository;

import com.goorm.wordsketch.entity.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {
}
