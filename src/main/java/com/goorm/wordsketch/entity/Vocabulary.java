package com.goorm.wordsketch.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Table(name = "vocabulary")
public class Vocabulary {

  // @GeneratedValue(strategy = GenerationType.xxx) : Primary Key의 키 생성
  // 전략(Strategy)을 설정하고자 할 때 사용
  // GenerationType.IDENTITY : DB의 AUTO_INCREMENT 방식을 이용
  // GenerationType.AUTO(default) : JPA 구현체(Hibernate)가 생성 방식을 결정
  // GenerationType.SEQUENCE : DB의 SEQUENCE를 이용해서 키를 생성. @SequenceGenerator와 같이 사용
  // GenerationType.TABLE : 키 생성 전용 테이블을 생성해서 키 생성. @TableGenerator와 함께 사용
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "vocabulary_id")
  private Long id;

  @Column(name = "substance", nullable = false, columnDefinition = "VARCHAR(100)")
  private String substance;

  @Column(name = "description", nullable = false, columnDefinition = "VARCHAR(300)")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private VocabularyType type;

  @OneToMany(mappedBy = "vocabulary")
  @Builder.Default
  private List<VocabularyContent> contents = new ArrayList<>();

  // Timestamp의 값을 현재 시간으로 자동 설정
  @Column(name = "created_date", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp createdDate;

  @Column(name = "last_modified_date", nullable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp lastModifiedDate;
}
