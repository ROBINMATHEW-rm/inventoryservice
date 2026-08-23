package com.inventoryservice.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {

    private String id;
    private String productId;
    private Integer availableQuantity;
    private Integer reservedQuantity;
}