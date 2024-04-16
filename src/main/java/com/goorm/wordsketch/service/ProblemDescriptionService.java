package com.goorm.wordsketch.service;

import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class ProblemDescriptionService {

  /**
   * 주어진 속담을 어절 단위로 나누어 한 어절을 빈칸 처리하는 함수
   * 
   * @param substance
   * @return
   */
  public String createProblemDescription(String substance) {

    // 입력받은 속담을 빈칸을 기준으로 파싱
    String[] strArr = substance.split(" ");

    // 빈칸으로 파싱한 문자열 배열 중, 랜덤한 순서의 문자열을 빈칸 처리
    int arrSize = strArr.length;
    int randomIdx = new Random().nextInt(arrSize);
    strArr[randomIdx].replaceAll(".", "_");

    // 변환한 속담 문제 지문을 반환
    return String.join(" ", strArr);
  }
}
