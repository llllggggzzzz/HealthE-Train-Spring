package com.conv.HealthETrain.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "knowledge-base-management")
public interface KnowledgeBaseClient {
}

