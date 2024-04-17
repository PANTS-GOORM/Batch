package com.goorm.wordsketch.batch;

import com.goorm.wordsketch.entity.Vocabulary;
import com.goorm.wordsketch.entity.VocabularyContent;
import com.goorm.wordsketch.entity.VocabularyType;
import com.goorm.wordsketch.service.OpenAIService;
import com.goorm.wordsketch.service.ProblemDescriptionService;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.HashMap;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

// Sprng Batch의 구성을 알기 쉽게 정리한 블로그: https://khj93.tistory.com/entry/Spring-Batch%EB%9E%80-%EC%9D%B4%ED%95%B4%ED%95%98%EA%B3%A0-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0
// Spring Batch 5 튜토리얼: https://spring.io/guides/gs/batch-processing
// DB에서 활용하기 위한 레퍼런스: https://docs.spring.io/spring-batch/docs/5.0.2/reference/html/readersAndWriters.html#readersAndWriters
// Youtube 자막에 포함된 단어를 기준으로 영상을 검색하는 사이트: https://youglish.com/korean
// 디버깅을 위해 참고중인 레퍼런스: https://docs.spring.io/spring-batch/docs/5.0.2/reference/html/whatsnew.html#datasource-transaction-manager-requirement-updates

@Configuration
@RequiredArgsConstructor
public class VocabularyContentSearchBatchConfiguration {

  private final EntityManagerFactory entityManagerFactory;
  private final OpenAIService openAIService;
  private final ProblemDescriptionService problemDescriptionService;

  @Bean
  public Job vocabularyContentSearchBatchJob(Step vocabularyContentSearchBatchJobStep,
      JobRepository jobRepository) {

    return new JobBuilder("vocabularyContentSearchBatchJob", jobRepository)
        .start(vocabularyContentSearchBatchJobStep)
        .incrementer(new RunIdIncrementer())
        .build();
  }

  @Bean
  @JobScope
  public Step vocabularyContentSearchBatchJobStep(JobRepository jobRepository,
      PlatformTransactionManager platformTransactionManager) {

    return new StepBuilder("vocabularyContentSearchBatchJobStep", jobRepository)
        .<Vocabulary, VocabularyContent>chunk(10, platformTransactionManager)
        .reader(vocabularyContentSearchBatchReader())
        .processor(jpaItemProcessor())
        .writer(vocabularyContentSearchBatchWriter())
        .build();
  }

  @Bean
  @StepScope
  public JpaPagingItemReader<Vocabulary> vocabularyContentSearchBatchReader() {

    Map<String, Object> parameterValues = new HashMap<>();
    parameterValues.put("type", VocabularyType.속담);

    String queryString = "SELECT v FROM Vocabulary v WHERE v.type = :type";

    return new JpaPagingItemReaderBuilder<Vocabulary>()
        .name("vocabularyContentSearchBatchReader")
        .entityManagerFactory(entityManagerFactory)
        .pageSize(10)
        .parameterValues(parameterValues)
        .queryString(queryString)
        .build();
  }

  @Bean
  public ItemProcessor<Vocabulary, VocabularyContent> jpaItemProcessor() {

    return vocabulary -> {

      // 해당 어휘에 이미 컨텐츠가 생성되어 있다면 스킵
      if (vocabulary.getContents().size() > 0)
        return null;

      // 속담을 openAI 서비스에게 8비트 이미지로 생성하도록 요청 후, S3 버킷에 저장한 결과물을 받아오기
      String contentURL = openAIService.getProverbImage(vocabulary.getSubstance());

      // 속담의 랜덤한 위치에 빈칸을 생성하는 서비스 호출
      String problemDescription = problemDescriptionService.createProblemDescription(vocabulary.getSubstance());

      return VocabularyContent.builder()
          .vocabulary(vocabulary)
          .contentURL(contentURL)
          .problemDescription(problemDescription)
          .build();
    };
  }

  @Bean
  public JpaItemWriter<VocabularyContent> vocabularyContentSearchBatchWriter() {
    JpaItemWriter<VocabularyContent> jpaItemWriter = new JpaItemWriter<>();
    jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
    return jpaItemWriter;
  }
}