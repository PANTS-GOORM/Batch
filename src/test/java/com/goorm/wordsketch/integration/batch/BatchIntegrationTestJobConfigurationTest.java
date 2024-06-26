package com.goorm.wordsketch.integration.batch;

import com.goorm.wordsketch.entity.Vocabulary;
import com.goorm.wordsketch.entity.VocabularyContent;
import com.goorm.wordsketch.entity.VocabularyType;
import com.goorm.wordsketch.repository.VocabularyContentRepository;
import com.goorm.wordsketch.repository.VocabularyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@SpringBatchTest
// 테스트 환경에서는 배치 어플리케이션 바로 실행되지 않도록 설정
@SpringBootTest(properties = {
    "spring.batch.job.enabled=false",
})
@DisplayName("어휘 관련 컨텐츠 수집 Batch Process 테스트")
public class BatchIntegrationTestJobConfigurationTest {

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired
  private VocabularyRepository vocabularyRepository;

  @Autowired
  private VocabularyContentRepository vocabularyContentRepository;

  @Nested
  @DisplayName("Given: DB에 컨텐츠 추가 대상 어휘가 존재할 때")
  class Given_DB에_컨텐츠_추가_대상_어휘가_존재할_때 {

    Vocabulary vocabulary = Vocabulary.builder()
        .substance("벼는 익을수록 고개를 숙인다")
        .description("익을수록 고개를 숙이는 벼 이삭에 빗대어, 많이 배우고 깨달은 사람일수록 교만하지 않고 겸손함을 표현하는 속담")
        .type(VocabularyType.속담)
        .build();

    Vocabulary savedVocabulary = vocabularyRepository.save(vocabulary);

    @Nested
    @DisplayName("When: 스케쥴로 지정된 시기가 되면")
    class When_스케쥴로_지정된_시기가_되면 {

      @Test
      @DisplayName("Then: 배치가 실행되며 대상 어휘와 관련된 컨텐츠를 찾아 추가한다.")
      void Then_배치가_실행되며_대상_어휘와_관련된_컨텐츠를_찾아_추가한다() throws Exception {

        // Job 실행 결과가 COMPLETE 인지 확인
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

        // Vocabulary Content의 개수가 1만큼 증가했는지 확인
        int vocabularyContentCount = vocabularyContentRepository.findAllByVocabulary(savedVocabulary)
            .orElseThrow()
            .size();

        assertSame(1, vocabularyContentCount);
      }
    }
  }

  @Nested
  @DisplayName("Given: DB에 컨텐츠 추가 대상 어휘가 없을 때")
  class Given_DB에_컨텐츠_추가_대상_어휘가_없을_때 {

    // 속담이 아닌 어휘 등록
    Vocabulary notProverbVocabulary = Vocabulary.builder()
        .substance("이듬해")
        .description("어떤 일이 있은 그 다음해. 익년(翌年).")
        .type(VocabularyType.단어)
        .build();

    // 속담이지만 이미 컨텐츠가 생성된 어휘 등록
    Vocabulary vocabularyWithContent = Vocabulary.builder()
        .substance("벼는 익을수록 고개를 숙인다")
        .description("익을수록 고개를 숙이는 벼 이삭에 빗대어, 많이 배우고 깨달은 사람일수록 교만하지 않고 겸손함을 표현하는 속담")
        .type(VocabularyType.속담)
        .build();

    VocabularyContent vocabularyContent = VocabularyContent.builder()
        .vocabulary(vocabularyWithContent)
        .contentURL("www.wordsketch.site")
        .problemDescription("테스트 지문입니다.")
        .build();

    Vocabulary savedNotProverbVocabulary = vocabularyRepository.save(notProverbVocabulary);
    Vocabulary savedVocabularyWithContent = vocabularyRepository.save(vocabularyWithContent);
    VocabularyContent savedVocabularyContent = vocabularyContentRepository.save(vocabularyContent);

    @Nested
    @DisplayName("When: 스케쥴로 지정된 시기가 되면")
    class When_스케쥴로_지정된_시기가_되면 {

      @Test
      @DisplayName("Then: 배치가 실행되지만, 변경 사항 없이 정상적으로 종료된다.")
      void Then_배치가_실행되지만_변경_사항_없이_정상적으로_종료된다() throws Exception {

        // Job 실행 결과가 COMPLETE 인지 확인
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

        // notProverbVocabulary의 contents 개수가 그대로인지 확인
        int updatedNotProverbVocabularyContentsCount = vocabularyContentRepository
            .findAllByVocabulary(savedNotProverbVocabulary)
            .orElseThrow()
            .size();

        assertSame(0, updatedNotProverbVocabularyContentsCount);

        // vocabularyWithContent의 contents 개수가 그대로인지 확인
        int updatedVocabularyWithContentsCount = vocabularyContentRepository
            .findAllByVocabulary(savedVocabularyWithContent)
            .orElseThrow()
            .size();

        assertSame(1, updatedVocabularyWithContentsCount);
      }
    }
  }
}
