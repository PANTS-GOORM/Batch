package com.goorm.wordsketch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WordsketchApplication {

  public static void main(String[] args) {

    System.exit(SpringApplication.exit(SpringApplication.run(WordsketchApplication.class, args)));
  }

}
