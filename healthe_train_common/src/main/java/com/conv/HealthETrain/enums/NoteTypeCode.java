package com.conv.HealthETrain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum NoteTypeCode {
    COMMENT(0,"评论"),
    NOTE_OF_REPOSITORY(1,"知识库笔记"),
    ASK(2,"社区提问"),
    ANSWER(3, "社区回答");


    @JsonValue
    private final Integer code;
    private final String message;

    NoteTypeCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}