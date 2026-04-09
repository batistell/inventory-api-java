package com.batistell.inventoryapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductEvent {
    
    private String eventId;
    private String action; // CREATE, UPDATE, DELETE
    private String productId;
    private Object productPayload; 
    private LocalDateTime timestamp;

}
