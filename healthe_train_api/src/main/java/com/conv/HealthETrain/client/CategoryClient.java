package com.conv.HealthETrain.client;


import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "information_portal")
public class CategoryClient {
}
