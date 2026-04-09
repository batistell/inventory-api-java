package com.batistell.inventoryapi.controller;

import com.batistell.inventoryapi.model.Inventory;
import com.batistell.inventoryapi.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{productId}")
    public ResponseEntity<Inventory> getInventory(@PathVariable String productId) {
        return inventoryService.getInventory(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Inventory> updateInventoryQuantity(
            @PathVariable String productId,
            @RequestParam Integer quantity) {
        try {
            Inventory updated = inventoryService.updateQuantity(productId, quantity);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Batch endpoint utilizing CompletableFutures and Virtual Threads for concurrent DB queries
     */
    @GetMapping("/batch-async")
    public CompletableFuture<ResponseEntity<List<Inventory>>> getInventoryBatchAsync(@RequestParam List<String> productIds) {
        log.info("Fetching {} inventory async records...", productIds.size());
        
        List<CompletableFuture<Inventory>> futures = productIds.stream()
                .map(inventoryService::getInventoryAsync)
                .toList();

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0]));

        return allFutures.thenApply(v -> {
            List<Inventory> results = futures.stream()
                    .map(CompletableFuture::join)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(results);
        });
    }

    /**
     * Standard batch endpoint
     */
    @GetMapping("/batch")
    public ResponseEntity<List<Inventory>> getInventoryBatch(@RequestParam List<String> productIds) {
        log.info("Fetching {} inventory records...", productIds.size());
        return ResponseEntity.ok(inventoryService.getInventoryBatch(productIds));
    }

    /**
     * Total Summary
     */
    @GetMapping("/summary")
    public CompletableFuture<ResponseEntity<Map<String, Long>>> getInventorySummary() {
        return CompletableFuture.supplyAsync(() -> {
            long total = inventoryService.getTotalItemsInStock();
            return ResponseEntity.ok(Map.of("totalItemsInStock", total));
        });
    }

}
