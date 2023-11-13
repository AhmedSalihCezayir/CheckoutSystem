package com.trendyol.checkout.models.entity.item;

import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
public class DefaultItem extends Item {
    private List<VasItem> vasItems;

    public DefaultItem() {
    }

    public DefaultItem(Item item) {
        super(item);
        if (item.getType() == null) {
            item.setType(ItemType.DEFAULT_ITEM);
        }
    }

    public DefaultItem(int itemId, int categoryId, int sellerId, double price, int quantity) {
        super(itemId, categoryId, sellerId, price, quantity, ItemType.DEFAULT_ITEM);
        vasItems = new ArrayList<>();
    }

    public DefaultItem(int itemId, int categoryId, int sellerId, double price, int quantity, List<VasItem> vasItems) {
        super(itemId, categoryId, sellerId, price, quantity, ItemType.DEFAULT_ITEM);
        this.vasItems = vasItems;
    }

    public List<VasItem> getVasItems() {
        return vasItems;
    }

    public void setVasItems(List<VasItem> vasItems) {
        this.vasItems = vasItems;
    }
}
