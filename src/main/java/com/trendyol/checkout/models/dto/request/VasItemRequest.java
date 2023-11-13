package com.trendyol.checkout.models.dto.request;

import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
public class VasItemRequest extends ItemRequest {
    private Integer vasItemId;

    public VasItemRequest() {
    }

    public VasItemRequest(Integer itemId, Integer categoryId, Integer sellerId, Double price, Integer quantity, Integer vasItemId) {
        super(itemId, categoryId, sellerId, price, quantity);
        this.vasItemId = vasItemId;
    }

    public Integer getVasItemId() {
        return vasItemId;
    }

    public boolean isValid() {
        return super.isValid() && vasItemId != null;
    }

    public List<String> getInvalidReasons() {
        List<String> reasons = super.getInvalidReasons();
        if (vasItemId == null) {
            reasons.add("VasItem ID is required");
        }

        return reasons;
    }

    @Override
    public String toString() {
        return "{" +
                "vasItemId=" + vasItemId + ", " +
                super.toString().substring(1);
    }
}
