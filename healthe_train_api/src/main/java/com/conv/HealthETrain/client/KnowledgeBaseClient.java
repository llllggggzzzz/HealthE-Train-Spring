package com.conv.HealthETrain.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "knowledge_base_management")
public interface KnowledgeBaseClient {
}

