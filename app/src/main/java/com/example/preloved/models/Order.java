package com.example.preloved.models;

public class Order {

    private int order_id;
    private int product_id;
    private int buyer_id;
    private int seller_id;
    private int offer_id;
    private String harga_final;
    private String status;
    private String created_at;

    private Product product;
    private User buyer;
    private User seller;

    public int getOrder_id() {
        return order_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public int getBuyer_id() {
        return buyer_id;
    }

    public int getSeller_id() {
        return seller_id;
    }

    public int getOffer_id() {
        return offer_id;
    }

    public String getHarga_final() {
        return harga_final;
    }

    public String getStatus() {
        return status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public Product getProduct() {
        return product;
    }

    public User getBuyer() {
        return buyer;
    }

    public User getSeller() {
        return seller;
    }
}
