package com.conv.HealthETrain.domain.DTO;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
public class StreamPublishInfo {
    private String serverId;
    private String serviceId;
    private String action;
    private String clientId;
    private String ip;
    private String vhost;
    private String app;
    private String tcUrl;
    private String stream;
    private String param;
    private String streamUrl;
    private String streamId;
}
