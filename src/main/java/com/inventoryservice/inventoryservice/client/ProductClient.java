package com.inventoryservice.inventoryservice.client;
import com.inventoryservice.inventoryservice.config.FeignConfig;
import com.inventoryservice.inventoryservice.dto.ProductClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "product-service",
        configuration = FeignConfig.class
)
public interface ProductClient {

    @GetMapping("/product-service/api/products/name/{name}")
    ProductClientResponse getProductByName(
            @PathVariable("name") String name);
}