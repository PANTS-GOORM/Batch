package com.goorm.wordsketch.repository;

import com.goorm.wordsketch.entity.VocabularyContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VocabularyContentRepository extends JpaRepository<VocabularyContent, Long> {
}
