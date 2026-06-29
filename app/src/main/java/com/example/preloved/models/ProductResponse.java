package com.example.preloved.models;

public class ProductResponse {
    private String message;
    private ProductData product; // Sesuai dengan key 'product' dari Laravel ProductController

    public String getMessage() { return message; }
    public ProductData getProduct() { return product; }

    public static class ProductData {
        private int product_id;
        public int getProductId() { return product_id; }
    }
}
