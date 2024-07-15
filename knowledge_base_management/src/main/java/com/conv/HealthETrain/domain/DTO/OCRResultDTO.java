package com.conv.HealthETrain.domain.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OCRResultDTO {
    @JsonProperty("words_result")
    private List<Word> wordsResult;

    @JsonProperty("words_result_num")
    private int wordsResultNum;

    @JsonProperty("log_id")
    private long logId;

    public static class Word {
        private String words;

        // Getter and Setter

        public String getWords() {
            return words;
        }

        public void setWords(String words) {
            this.words = words;
        }
    }
}

