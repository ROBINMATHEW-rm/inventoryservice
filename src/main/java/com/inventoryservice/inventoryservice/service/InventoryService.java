package com.inventoryservice.inventoryservice.service;

import com.inventoryservice.inventoryservice.client.ProductClient;
import com.inventoryservice.inventoryservice.dto.InventoryRequest;
import com.inventoryservice.inventoryservice.dto.InventoryResponse;
import com.inventoryservice.inventoryservice.dto.ProductClientResponse;
import com.inventoryservice.inventoryservice.entity.Inventory;
import com.inventoryservice.inventoryservice.exception.ProductNotFoundException;
import com.inventoryservice.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductClient productClient;

    public InventoryResponse createInventory(InventoryRequest request) {
        ProductClientResponse product;
        try {
             product =
                    productClient.getProductByName(request.getProductName());
        } catch (ProductNotFoundException ex) {

        throw new ProductNotFoundException(
                "Product not found: " + request.getProductName());
        }
        Inventory inventory = new Inventory();

        inventory.setProductId(product.getId());
        inventory.setProductName(product.getName());
        inventory.setAvailableQuantity(request.getAvailableQuantity());
        inventory.setReservedQuantity(request.getReservedQuantity());

        Inventory savedInventory = inventoryRepository.save(inventory);

        return mapToResponse(savedInventory);
    }

    public List<InventoryResponse> getAllInventory() {

        return inventoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public InventoryResponse getInventoryByProductId(String productId) {

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Inventory not found for product: " + productId));

        return mapToResponse(inventory);
    }

    public void deleteInventory(String id) {

        inventoryRepository.deleteById(id);
    }

    private InventoryResponse mapToResponse(Inventory inventory) {

        return new InventoryResponse(
                inventory.getId(),
                inventory.getProductId(),
                inventory.getProductName(),
                inventory.getAvailableQuantity(),
                inventory.getReservedQuantity()
        );
    }
}