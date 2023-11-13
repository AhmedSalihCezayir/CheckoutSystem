package com.trendyol.checkout.models.dto.response;

import com.trendyol.checkout.models.entity.cart.Cart;
import com.trendyol.checkout.models.entity.item.Item;
import com.trendyol.checkout.models.entity.item.ItemType;

import java.util.List;
import java.util.stream.Collectors;

public class CartResponse {
    private List<Item> items;
    private Double totalPrice;
    private Integer appliedPromotionId;
    private Double totalDiscount;


    public CartResponse(Cart cart) {
        this.items = cart.getItems().stream().filter(i->i.getType() != ItemType.VAS_ITEM).collect(Collectors.toList());
        this.totalPrice = cart.getTotalPrice() - cart.getTotalDiscount();
        this.appliedPromotionId = cart.getAppliedPromotionId();
        this.totalDiscount = cart.getTotalDiscount();
    }

    public CartResponse(List<Item> items, Double totalPrice, Integer appliedPromotionId, Double totalDiscount) {
        this.items = items;
        this.totalPrice = totalPrice;
        this.appliedPromotionId = appliedPromotionId;
        this.totalDiscount = totalDiscount;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
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

    @Override
    public String toString() {
        return "CartResponse{" +
                "items=" + items +
                ", totalPrice=" + totalPrice +
                ", appliedPromotionId=" + appliedPromotionId +
                ", totalDiscount=" + totalDiscount +
                '}';
    }
}
