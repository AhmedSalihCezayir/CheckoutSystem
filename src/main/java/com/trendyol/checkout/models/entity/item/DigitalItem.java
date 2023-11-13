package com.trendyol.checkout.models.entity.item;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public class DigitalItem extends Item {
    public DigitalItem() {
    }

    public DigitalItem(Item item) {
        super(item);
        if (item.getType() == null) {
            item.setType(ItemType.DIGITAL_ITEM);
        }
    }

    public DigitalItem(int itemId, int categoryId, int sellerId, double price, int quantity) {
        super(itemId, categoryId, sellerId, price, quantity, ItemType.DIGITAL_ITEM);
    }
}
