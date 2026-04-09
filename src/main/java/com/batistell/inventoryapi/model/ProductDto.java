package com.batistell.inventoryapi.model;

import lombok.Data;

@Data
public class ProductDto {
    private String id;
    private String name;
    private String description;
    private Double price;
}
