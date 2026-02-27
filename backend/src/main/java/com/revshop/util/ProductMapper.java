package com.revshop.util;

import com.revshop.dto.ProductDto;
import com.revshop.model.Product;

public class ProductMapper {

    private ProductMapper() {
        // Utility class
    }

    public static ProductDto mapToDto(Product p) {
        return new ProductDto(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getQuantity(),
                p.getRating());
    }
}
