package com.batistell.inventoryapi.service;

import com.batistell.inventoryapi.model.Inventory;
import com.batistell.inventoryapi.repository.InventoryRepository;
import com.batistell.inventoryapi.client.CatalogClient;
import com.batistell.inventoryapi.model.ProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final CatalogClient catalogClient;

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

    public final CatalogClient getCatalogClient() { return this.catalogClient; }

    public long getTotalItemsInStock() {
        return inventoryRepository.findAll().stream()
                .mapToLong(Inventory::getQuantity)
                .sum();
    }

    public List<Object[]> getStockAggregation() {
        return inventoryRepository.getStockAggregationByStatus();
    }

    public List<Inventory> getLowStockItems(int threshold) {
        return inventoryRepository.findLowStockItems(threshold);
    }

    public Map<String, Object> getInventoryWithProductDetails(String productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId).orElse(null);
        ProductDto product = null;
        try {
            product = catalogClient.getProductById(productId);
        } catch (Exception e) {
            log.error("Failed to fetch product details for {}", productId, e);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("inventory", inventory);
        response.put("product", product);
        return response;
    }
}
