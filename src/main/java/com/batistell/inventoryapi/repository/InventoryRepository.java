package com.batistell.inventoryapi.repository;

import com.batistell.inventoryapi.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductId(String productId);
    List<Inventory> findByProductIdIn(List<String> productIds);
    boolean existsByProductId(String productId);

    @org.springframework.data.jpa.repository.Query(
        value = "SELECT status, COUNT(*), SUM(quantity) FROM inventory GROUP BY status",
        nativeQuery = true
    )
    List<Object[]> getStockAggregationByStatus();

    @org.springframework.data.jpa.repository.Query(
        value = "SELECT * FROM inventory WHERE quantity < :threshold ORDER BY updated_at DESC", 
        nativeQuery = true
    )
    List<Inventory> findLowStockItems(@org.springframework.data.repository.query.Param("threshold") int threshold);
}
