package com.inventoryservice.inventoryservice.repository;

import com.inventoryservice.inventoryservice.entity.Inventory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface InventoryRepository extends MongoRepository<Inventory, String> {

    Optional<Inventory> findByProductId(String productId);
}