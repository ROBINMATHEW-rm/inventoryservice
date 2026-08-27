package com.inventoryservice.inventoryservice.exception;

import feign.Response;
import feign.codec.ErrorDecoder;

public class ProductServiceErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {

        if (response.status() == 404) {
            return new ProductNotFoundException(
                    "Product not found in Product Service");
        }

        return new RuntimeException(
                "Product Service error: " + response.status());
    }
}