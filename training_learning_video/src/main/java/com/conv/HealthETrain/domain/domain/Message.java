package com.conv.HealthETrain.domain.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class Message {
    private String from;
    private String streamId;
    private String userName;
    private String message;
    private String date;
    private Integer type;
}
