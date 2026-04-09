package com.batistell.inventoryapi.service;

import com.batistell.inventoryapi.model.Inventory;
import com.batistell.inventoryapi.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public Inventory createInventoryForProduct(String productId) {
        if (inventoryRepository.existsByProductId(productId)) {
            log.warn("Inventory for productId={} already exists. Ignoring creation.", productId);
            return inventoryRepository.findByProductId(productId).get();
        }

        Inventory inventory = Inventory.builder()
                .productId(productId)
                .quantity(0)
                .build();
        return inventoryRepository.save(inventory);
    }

    public Optional<Inventory> getInventory(String productId) {
        return inventoryRepository.findByProductId(productId);
    }

    @Transactional
    public Inventory updateQuantity(String productId, Integer newQuantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + productId));
        
        inventory.setQuantity(newQuantity);
        inventory.updateStatus();
        return inventoryRepository.save(inventory);
    }

    @Async
    public CompletableFuture<Inventory> getInventoryAsync(String productId) {
        return CompletableFuture.completedFuture(
                inventoryRepository.findByProductId(productId).orElse(null)
        );
    }

    public List<Inventory> getInventoryBatch(List<String> productIds) {
        return inventoryRepository.findByProductIdIn(productIds);
    }

    public long getTotalItemsInStock() {
        return inventoryRepository.findAll().stream()
                .mapToLong(Inventory::getQuantity)
                .sum();
    }
}
