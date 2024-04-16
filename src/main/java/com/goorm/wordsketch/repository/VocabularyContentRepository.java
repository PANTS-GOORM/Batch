package com.goorm.wordsketch.repository;

import java.util.List;
import java.util.Optional;

import com.goorm.wordsketch.entity.Vocabulary;
import com.goorm.wordsketch.entity.VocabularyContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VocabularyContentRepository extends JpaRepository<VocabularyContent, Long> {

  Optional<List<VocabularyContent>> findAllByVocabulary(Vocabulary vocabulary);
}
