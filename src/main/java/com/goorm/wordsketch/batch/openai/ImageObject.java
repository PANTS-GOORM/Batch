package com.goorm.wordsketch.batch.openai;

import lombok.Data;

@Data
public class ImageObject {

    String b64_json;
    String revised_prompt;
}
