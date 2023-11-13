package com.trendyol.checkout.service;

import com.trendyol.checkout.CartConstants;
import com.trendyol.checkout.exception.ItemNotFoundException;
import com.trendyol.checkout.validation.ItemValidator;
import com.trendyol.checkout.promotion.PromotionCalculator;
import com.trendyol.checkout.models.entity.cart.Cart;
import com.trendyol.checkout.models.dto.request.ItemRequest;
import com.trendyol.checkout.models.dto.request.VasItemRequest;
import com.trendyol.checkout.models.dto.response.CartResponse;
import com.trendyol.checkout.models.dto.response.BasicResponse;
import com.trendyol.checkout.models.entity.item.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {
    private ItemValidator validator;
    private PromotionCalculator promotionCalculator;
    private Cart cart;

    @Autowired
    public CartServiceImpl(Cart cart, ItemValidator validator, PromotionCalculator promotionCalculator) {
        this.validator = validator;
        this.cart = cart;
        this.promotionCalculator = promotionCalculator;
    }

    public ResponseEntity<BasicResponse> addNonVasItemToCart(ItemRequest request) {
        validator.validateNonVasItemRequest(request);
        Item item = itemConverter(request);
        addItemToCart(item);

        String message = "Item with id: " + request.getItemId() + " successfully added to cart";
        BasicResponse response = new BasicResponse(message, true);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    public ResponseEntity<BasicResponse> addVasItemToCart(VasItemRequest request) {
        validator.validateVasItemRequest(request);
        Item item = itemConverter(request);
        addItemToCart(item);
        addVasItemToDefaultItem((VasItem) item, request.getItemId());

        String message = "Item with id: " + request.getVasItemId() + " successfully added to cart";
        BasicResponse response = new BasicResponse(message, true);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    public ResponseEntity<BasicResponse> removeItem(int itemId) {
        Optional<Item> item = cart.findItemById(itemId);
        if (item.isEmpty()) {
            throw new ItemNotFoundException("There is no item with id: " + itemId + " in your cart");
        }

        decreaseCartProperties(item.get());

        if (item.get().getType() == ItemType.DEFAULT_ITEM && !((DefaultItem) item.get()).getVasItems().isEmpty()) {
            for (VasItem vasItem : ((DefaultItem) item.get()).getVasItems()) {
                decreaseCartProperties(vasItem);
            }
        }

        cart.getItems().removeIf(i -> i.getItemId() == itemId);
        if (cart.getItems().isEmpty()) {
            cart.setDigitalItemCart(null);
        }

        String message = "Item with id " + itemId + " is successfully removed from cart";
        BasicResponse response = new BasicResponse(message, true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<BasicResponse> resetCart() {
        cart.reset();

        BasicResponse response = new BasicResponse("The cart has been successfully reset", true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<BasicResponse> displayCart() {
        HashMap<String, Number> bestPromotion = promotionCalculator.calculateMaxPromotion(cart.getItems(), cart.getTotalPrice());
        cart.setAppliedPromotionId((Integer) bestPromotion.get("promotionId"));
        cart.setTotalDiscount((Double) bestPromotion.get("discount"));

        CartResponse cartResponse = new CartResponse(cart);
        BasicResponse response = new BasicResponse(cartResponse, true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void decreaseCartProperties(Item item) {
        cart.setItemCount(cart.getItemCount() - item.getQuantity());
        cart.setTotalPrice(cart.getTotalPrice() - item.getQuantity() * item.getPrice());
        cart.getItems().removeIf(i -> i.getItemId() == item.getItemId());

        if (item.getType() != ItemType.VAS_ITEM) {
            cart.setUniqueItemCount(cart.getUniqueItemCount() - 1);
        }
    }

    private void addItemToCart(Item item) {
        Optional<Item> optionalExistingItem = cart.findItemById(item.getItemId());

        cart.setTotalPrice(cart.getTotalPrice() + item.getPrice() * item.getQuantity());
        cart.setItemCount(cart.getItemCount() + item.getQuantity());

        if (optionalExistingItem.isPresent()) {
            Item existingItem = optionalExistingItem.get();
            existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
        } else {
            cart.getItems().add(item);
            updateCartUniqueItemCount(item);
        }

        cart.setDigitalItemCart(item.getType() == ItemType.DIGITAL_ITEM);
    }

    private void addVasItemToDefaultItem(VasItem vasItem, int defaultItemId) {
        Optional<DefaultItem> defaultItemOptional = cart.getItems().stream()
                .filter(i -> i.getClass() == DefaultItem.class)
                .filter(i -> i.getItemId() == defaultItemId)
                .findFirst()
                .map(i -> (DefaultItem) i);

        defaultItemOptional.ifPresent(defaultItem -> {
            updateOrAddVasItemToDefaultItem(defaultItem, vasItem);
        });
    }

    private void updateOrAddVasItemToDefaultItem(DefaultItem defaultItem, VasItem vasItem) {
        defaultItem.getVasItems().stream()
                .filter(i -> i.getItemId() == vasItem.getItemId())
                .findFirst()
                .ifPresentOrElse(
                        existingVasItem -> existingVasItem.setQuantity(existingVasItem.getQuantity() + vasItem.getQuantity()),
                        () -> defaultItem.getVasItems().add(new VasItem(vasItem))
                );
    }

    private void updateCartUniqueItemCount(Item item) {
        if (!(item instanceof VasItem)) {
            cart.setUniqueItemCount(cart.getUniqueItemCount() + 1);
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
