package com.goorm.wordsketch.batch.openai;

import java.util.List;
import lombok.Data;

@Data
public class OpenAIResponse {

    String created;
    List<ImageObject> data;
}
