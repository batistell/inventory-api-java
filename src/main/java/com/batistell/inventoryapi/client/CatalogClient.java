package com.batistell.inventoryapi.client;

import com.batistell.inventoryapi.model.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog-api", url = "${catalog.api.url}")
public interface CatalogClient {

    @GetMapping("/api/catalog/products/{id}")
    ProductDto getProductById(@PathVariable("id") String id);
}
