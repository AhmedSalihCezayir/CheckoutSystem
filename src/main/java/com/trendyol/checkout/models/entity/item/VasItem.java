package com.trendyol.checkout.models.entity.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class VasItem extends Item {

    @JsonProperty("vasItemId")
    public int getVasItemId() {
        return super.getItemId();
    }

    @JsonIgnore
    @Override
    public int getItemId() {
        return super.getItemId();
    }

    public VasItem() {
    }

    public VasItem(Item item) {
        super(item);
        if (item.getType() == null) {
            item.setType(ItemType.VAS_ITEM);
        }
    }

    public VasItem(int itemId, int categoryId, int sellerId, double price, int quantity) {
        super(itemId, categoryId, sellerId, price, quantity, ItemType.VAS_ITEM);
    }
}
