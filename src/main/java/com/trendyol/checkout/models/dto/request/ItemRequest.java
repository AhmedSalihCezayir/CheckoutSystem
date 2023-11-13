package com.trendyol.checkout.models.dto.request;

import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
public class ItemRequest {
    private Integer itemId;
    private Integer categoryId;
    private Integer sellerId;
    private Double price;
    private Integer quantity;

    public ItemRequest() {
    }

    public ItemRequest(Integer itemId, Integer categoryId, Integer sellerId, Double price, Integer quantity) {
        this.itemId = itemId;
        this.categoryId = categoryId;
        this.sellerId = sellerId;
        this.price = price;
        this.quantity = quantity;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public boolean isValid() {
        return this.itemId != null
                && this.categoryId != null
                && this.sellerId != null
                && this.price != null && this.price > 0
                && this.quantity != null && this.quantity > 0;
    }

    public List<String> getInvalidReasons() {
        List<String> reasons = new ArrayList<>();

        if (this.itemId == null) {
            reasons.add("Item ID is required");
        }

        if (this.categoryId == null) {
            reasons.add("Category ID is required");
        }

        if (this.sellerId == null) {
            reasons.add("Seller ID is required");
        }

        if (this.price == null || this.price <= 0) {
            reasons.add("Price must be greater than 0");
        }

        if (this.quantity == null || this.quantity <= 0) {
            reasons.add("Quantity must be greater than 0");
        }

        return reasons;
    }

    @Override
    public String toString() {
        return "{" +
                "itemId=" + itemId +
                ", categoryId=" + categoryId +
                ", sellerId=" + sellerId +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
