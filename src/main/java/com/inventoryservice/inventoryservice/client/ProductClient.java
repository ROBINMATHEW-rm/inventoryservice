package com.inventoryservice.inventoryservice.client;
import com.inventoryservice.inventoryservice.dto.ProductClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "product-service",
        url = "${product-service.url}"
)
public interface ProductClient {

    @GetMapping("/api/products/name/{name}")
    ProductClientResponse getProductByName(
            @PathVariable("name") String name);
}