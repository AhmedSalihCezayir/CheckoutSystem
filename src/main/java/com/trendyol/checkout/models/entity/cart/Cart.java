package com.trendyol.checkout.models.entity.cart;

import com.trendyol.checkout.models.entity.item.Item;
import com.trendyol.checkout.models.entity.item.ItemType;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Cart {
    private int id;
    private List<Item> items;
    private Integer itemCount;
    private Integer uniqueItemCount;
    private Double totalPrice;
    private Integer appliedPromotionId;
    private Double totalDiscount;
    private Boolean isDigitalItemCart;

    public Cart() {
        this.items = new ArrayList<>();
        this.itemCount = 0;
        this.uniqueItemCount = 0;
        this.totalPrice = 0.0;
        this.totalDiscount = 0.0;
    }

    public Cart(List<Item> items, Integer itemCount, Integer uniqueItemCount, Double totalPrice, Integer appliedPromotionId, Double totalDiscount, Boolean isDigitalItemCart) {
        this.items = items;
        this.itemCount = itemCount;
        this.uniqueItemCount = uniqueItemCount;
        this.totalPrice = totalPrice;
        this.appliedPromotionId = appliedPromotionId;
        this.totalDiscount = totalDiscount;
        this.isDigitalItemCart = isDigitalItemCart;
    }

    public Cart(int id, List<Item> items, Integer itemCount, Integer uniqueItemCount, Double totalPrice, Integer appliedPromotionId, Double totalDiscount, Boolean isDigitalItemCart) {
        this.id = id;
        this.items = items;
        this.itemCount = itemCount;
        this.uniqueItemCount = uniqueItemCount;
        this.totalPrice = totalPrice;
        this.appliedPromotionId = appliedPromotionId;
        this.totalDiscount = totalDiscount;
        this.isDigitalItemCart = isDigitalItemCart;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
        this.uniqueItemCount = (int) items.stream()
                .filter(i -> i.getType() != ItemType.VAS_ITEM)
                .map(Item::getItemId).distinct().count();
        this.isDigitalItemCart = !items.isEmpty() && items.get(0).getType() == ItemType.DIGITAL_ITEM;

        for (Item item : items) {
            this.totalPrice += item.getPrice() * item.getQuantity();
            this.itemCount += item.getQuantity();
        }
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    public Integer getUniqueItemCount() {
        return uniqueItemCount;
    }

    public void setUniqueItemCount(Integer uniqueItemCount) {
        this.uniqueItemCount = uniqueItemCount;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getAppliedPromotionId() {
        return appliedPromotionId;
    }

    public void setAppliedPromotionId(Integer appliedPromotionId) {
        this.appliedPromotionId = appliedPromotionId;
    }

    public Double getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(Double totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public Boolean isDigitalItemCart() {
        return isDigitalItemCart;
    }

    public void setDigitalItemCart(Boolean digitalItemCart) {
        isDigitalItemCart = digitalItemCart;
    }

    public Optional<Item> findItemById(Integer itemId) {
        return this.items.stream().filter(item -> item.getItemId() == itemId).findFirst();
    }

    public void reset() {
        this.items = new ArrayList<>();
        this.itemCount = 0;
        this.uniqueItemCount = 0;
        this.totalPrice = 0.0;
        this.appliedPromotionId = null;
        this.totalDiscount = 0.0;
        this.isDigitalItemCart = null;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "items=" + items +
                ", itemCount=" + itemCount +
                ", uniqueItemCount=" + uniqueItemCount +
                ", totalPrice=" + totalPrice +
                ", appliedPromotionId=" + appliedPromotionId +
                ", totalDiscount=" + totalDiscount +
                ", isDigitalItemCart=" + isDigitalItemCart +
                '}';
    }
}
