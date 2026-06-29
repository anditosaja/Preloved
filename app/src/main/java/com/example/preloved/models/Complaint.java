package com.example.preloved.models;
import com.google.gson.annotations.SerializedName;

public class Complaint {
    @SerializedName("id") private int id;
    @SerializedName("ticket_id") private String ticket_id;
    @SerializedName("subject") private String subject;
    @SerializedName("description") private String description;
    @SerializedName("status") private String status;
    @SerializedName("created_at") private String created_at;

    private User user;
    private Product product;

    // Getter tetap sama
    public int getId() { return id; }
    public String getTicketId() { return ticket_id; }
    public String getSubject() { return subject; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return created_at; }
    public User getUser() { return user; }
    public Product getProduct() { return product; }
}
