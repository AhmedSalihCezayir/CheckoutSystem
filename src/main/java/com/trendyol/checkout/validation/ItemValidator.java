package com.trendyol.checkout.validation;

import com.trendyol.checkout.CartConstants;
import com.trendyol.checkout.models.entity.cart.Cart;
import com.trendyol.checkout.exception.InvalidCartStateException;
import com.trendyol.checkout.exception.InvalidCartItemException;
import com.trendyol.checkout.models.dto.request.ItemRequest;
import com.trendyol.checkout.models.dto.request.VasItemRequest;
import com.trendyol.checkout.models.entity.item.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ItemValidator {

    private Cart cart;

    @Autowired
    public ItemValidator(Cart cart) {
        this.cart = cart;
    }

    public void validateNonVasItemRequest(ItemRequest request) {
        validateRequestParametersNotEmpty(request);
        validateNonVasItemCategoryAndSeller(request.getCategoryId(), request.getSellerId());
        validateCartTotalItemCountAndPrice(request);

        Item item = itemConverter(request);
        validateIndividualItemCount(item);
        validateAddedItemTypeMatchesCartType(item);

        validateTotalUniqueItemCountInCart(request);
    }

    public void validateVasItemRequest(VasItemRequest request) {
        validateRequestParametersNotEmpty(request);
        validateVasItemCategoryAndSeller(request.getCategoryId(), request.getSellerId());
        validateCartTotalItemCountAndPrice(request);

        Item item = itemConverter(request);
        validateIndividualItemCount(item);

        Optional<Item> relatedDefaultItem = cart.getItems().stream()
                .filter(i -> i.getItemId() == request.getItemId())
                .filter(i -> i.getClass() == DefaultItem.class)
                .findFirst();
        validateDefaultItemOfVasItem(request, relatedDefaultItem);
    }

    private void validateRequestParametersNotEmpty(ItemRequest request) {
        if (!request.isValid()) {
            List<String> reasons = request.getInvalidReasons();
            throw new IllegalArgumentException("Invalid request parameter(s). Fail reason: " + reasons);
        }
    }

    private void validateCartTotalItemCountAndPrice(ItemRequest request) {
        int newTotalItemCount = cart.getItemCount() + request.getQuantity();
        double newCartTotalPrice = cart.getTotalPrice() + (request.getPrice() * request.getQuantity());

        if (newTotalItemCount > CartConstants.MAX_TOTAL_ITEM_COUNT_IN_CART) {
            throw new InvalidCartStateException("There can be maximum " + CartConstants.MAX_TOTAL_ITEM_COUNT_IN_CART + " items in the cart");
        }

        if (newCartTotalPrice > CartConstants.MAX_CART_TOTAL_PRICE) {
            throw new InvalidCartStateException("Cart total can be maximum " + CartConstants.MAX_CART_TOTAL_PRICE);
        }
    }

    private void validateIndividualItemCount(Item item) {
        Optional<Item> oldItem = cart.findItemById(item.getItemId());
        int oldItemCount = oldItem.map(Item::getQuantity).orElse(0);
        int newItemCount = oldItemCount + item.getQuantity();

        Map<ItemType, Integer> maxCounts = Map.of(
                ItemType.DIGITAL_ITEM, CartConstants.DIGITAL_ITEM_MAX_COUNT_IN_CART,
                ItemType.DEFAULT_ITEM, CartConstants.DEFAULT_ITEM_MAX_COUNT_IN_CART,
                ItemType.VAS_ITEM, CartConstants.VAS_ITEM_MAX_COUNT_IN_CART
        );

        ItemType itemType = item.getType();

        if (newItemCount > maxCounts.get(itemType)) {
            throw new InvalidCartStateException("Each " + itemType.toString().toLowerCase() + " can be added maximum of " + maxCounts.get(itemType) + " times to the cart.");
        }
    }

    private void validateTotalUniqueItemCountInCart(ItemRequest request) {
        Optional<Item> alreadyExistingItem = cart.findItemById(request.getItemId());
        if ((alreadyExistingItem.isEmpty()) && cart.getUniqueItemCount() >= CartConstants.MAX_UNIQUE_ITEM_TYPE_COUNT_IN_CART) {
            throw new InvalidCartStateException("There can be maximum " + CartConstants.MAX_UNIQUE_ITEM_TYPE_COUNT_IN_CART + " unique items in the cart");
        }
    }

    private void validateAddedItemTypeMatchesCartType(Item item) {
        if (cart.isDigitalItemCart() != null &&
                ((cart.isDigitalItemCart() && item.getType() != ItemType.DIGITAL_ITEM)
                        || (!cart.isDigitalItemCart() && item.getType() == ItemType.DIGITAL_ITEM))
        ) {
            String cartItemType = cart.isDigitalItemCart() ? "digital" : "default";
            throw new InvalidCartStateException("Cart cannot hold both digital and default items. Current cart is holding " + cartItemType + " items.");  // TODO name of this error might change
        }
    }

    private void validateNonVasItemCategoryAndSeller(int categoryId, int sellerId) {
        if (categoryId == CartConstants.VAS_ITEM_CATEGORY_ID) {
            throw new InvalidCartItemException("Cannot add item with categoryId: " + categoryId + ". This categoryId is only for VasItems");
        }

        if (sellerId == CartConstants.VAS_ITEM_SELLER_ID) {
            throw new InvalidCartItemException("Cannot add item with sellerId: " + sellerId + ". This sellerId is only for VasItems");
        }
    }

    private void validateVasItemCategoryAndSeller(int categoryId, int sellerId) {
        if (categoryId != CartConstants.VAS_ITEM_CATEGORY_ID) {
            throw new InvalidCartItemException("Cannot add VasItem with categoryId: " + categoryId +
                    ". VasItems should have categoryId of " + CartConstants.VAS_ITEM_CATEGORY_ID);
        }

        if (sellerId != CartConstants.VAS_ITEM_SELLER_ID) {
            throw new InvalidCartItemException("Cannot add item with sellerId: " + sellerId +
                    ". VasItems should have sellerId of " + CartConstants.VAS_ITEM_SELLER_ID);
        }
    }

    private void validateDefaultItemOfVasItem(VasItemRequest request, Optional<Item> relatedDefaultItem) {
        Optional<Item> existing = cart.getItems().stream().filter(i -> i.getItemId() == request.getVasItemId()).findFirst();
        int newUpdatedVasItemQuantity = existing.map(item -> item.getQuantity() + request.getQuantity()).orElseGet(request::getQuantity);


        if (relatedDefaultItem.isEmpty()) {
            throw new InvalidCartItemException("There is no default item in your cart with the given id: " + request.getItemId());
        } else if (!CartConstants.CATEGORY_IDS_FOR_DEFAULT_ITEMS_THAT_CAN_HAVE_VAS_ITEM.contains(relatedDefaultItem.get().getCategoryId())) {
            throw new InvalidCartItemException("You can only add vasItem to default items with category ids: " + CartConstants.CATEGORY_IDS_FOR_DEFAULT_ITEMS_THAT_CAN_HAVE_VAS_ITEM);
        } else if (newUpdatedVasItemQuantity > CartConstants.MAX_VAS_ITEM_COUNT_FOR_A_DEFAULT_ITEM) {
            throw new InvalidCartStateException("You cannot add more than " + CartConstants.MAX_VAS_ITEM_COUNT_FOR_A_DEFAULT_ITEM + " VasItem to default items");
        } else if (relatedDefaultItem.get().getPrice() < request.getPrice()) {
            throw new InvalidCartItemException("VasItem price cannot be more than default item price. " +
                    "VasItem price: " + request.getPrice() + ", Default item price: " + relatedDefaultItem.get().getPrice());
        }
    }

    private Item itemConverter(ItemRequest request) {
        if (request.getCategoryId() == CartConstants.DIGITAL_ITEM_CATEGORY_ID) {
            return new DigitalItem(request.getItemId(),
                    request.getCategoryId(),
                    request.getSellerId(),
                    request.getPrice(),
                    request.getQuantity());
        } else if (request.getCategoryId() == CartConstants.VAS_ITEM_CATEGORY_ID) {
            return new VasItem(((VasItemRequest) request).getVasItemId(),
                    request.getCategoryId(),
                    request.getSellerId(),
                    request.getPrice(),
                    request.getQuantity());
        } else {
            return new DefaultItem(request.getItemId(),
                    request.getCategoryId(),
                    request.getSellerId(),
                    request.getPrice(),
                    request.getQuantity());
        }
    }
}
