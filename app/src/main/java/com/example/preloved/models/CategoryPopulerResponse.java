package com.example.preloved.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CategoryPopulerResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<Category> data;
    public String getStatus() {
        return status;
    }

    public List<Category> getData() {
        return data;
    }
}
