package com.trendyol.checkout.models.entity.item;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract class Item {
    private int itemId;
    private int categoryId;
    private int sellerId;
    private double price;
    private int quantity;
    private ItemType type;

    public Item() {
    }

    public Item(int itemId, int categoryId, int sellerId, double price, int quantity, ItemType type) {
        this.itemId = itemId;
        this.categoryId = categoryId;
        this.sellerId = sellerId;
        this.price = price;
        this.quantity = quantity;
        this.type = type;
    }

    public Item(Item copiedItem) {
        this.itemId = copiedItem.itemId;
        this.categoryId = copiedItem.categoryId;
        this.sellerId = copiedItem.sellerId;
        this.price = copiedItem.price;
        this.quantity = copiedItem.quantity;
        this.type = copiedItem.type;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Item{" +
                "itemId=" + itemId +
                ", categoryId=" + categoryId +
                ", sellerId=" + sellerId +
                ", price=" + price +
                ", quantity=" + quantity +
                ", type=" + type +
                '}';
    }
}
