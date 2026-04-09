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
}
