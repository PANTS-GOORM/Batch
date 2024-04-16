package com.goorm.wordsketch.unit.batch;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.goorm.wordsketch.service.ProblemDescriptionService;

@DisplayName("컨텐츠 문제 지문 테스트")
public class ProblemDescriptionServiceTest {

  private ProblemDescriptionService problemDescriptionService = new ProblemDescriptionService();

  @Nested
  @DisplayName("Given: 임의의 속담이 주어질 때")
  class Given_임의의_속담이_주어질_때 {

    private String proverb = "벼는 익을수록 고개를 숙인다";

    @Nested
    @DisplayName("When: createProblemDescription 함수의 인자로 넘겨주면")
    class When_createProblemDescription_함수의_인자로_넘겨주면 {

      String result = problemDescriptionService.createProblemDescription(proverb);

      @Test
      @DisplayName("Then: 한 어절이 빈칸인 문자열을 반환한다.")
      void Then_한_어절이_빈칸인_문자열을_반환한다() {

        assertTrue(result.contains("_"));

        // 어절을 빈칸으로 잘 구분하는지도 확인
        assertTrue(result.contains(" "));
      }
    }
  }
}
