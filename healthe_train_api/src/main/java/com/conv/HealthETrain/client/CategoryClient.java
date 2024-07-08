package com.conv.HealthETrain.client;


import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "information-portal")
public interface CategoryClient {
}
