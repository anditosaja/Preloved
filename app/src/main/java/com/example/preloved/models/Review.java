package com.example.preloved.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Review implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("order_id")
    private int orderId;

    @SerializedName("reviewer_id")
    private int reviewerId;

    @SerializedName("seller_id")
    private int sellerId;

    @SerializedName("product_id")
    private int productId;

    @SerializedName("rating")
    private int rating;

    @SerializedName("comment")
    private String comment;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("reviewer")
    private User reviewer;

    @SerializedName("seller")
    private User seller;

    // [TAMBAHAN BARU] Menangkap objek product dari Laravel
    @SerializedName("product")
    private Product product;

    // ==========================================
    // GETTER METHODS
    // ==========================================
    public int getId() { return id; }
    public int getOrderId() { return orderId; }
    public int getReviewerId() { return reviewerId; }
    public int getSellerId() { return sellerId; }
    public int getProductId() { return productId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getCreatedAt() { return createdAt; }
    public User getReviewer() { return reviewer; }
    public User getSeller() { return seller; }

    // Getter untuk Product
    public Product getProduct() { return product; }
}
