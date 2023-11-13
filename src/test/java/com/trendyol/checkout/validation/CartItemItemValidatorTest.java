package com.trendyol.checkout.validation;

import com.trendyol.checkout.exception.InvalidCartItemException;
import com.trendyol.checkout.exception.InvalidCartStateException;
import com.trendyol.checkout.models.dto.request.ItemRequest;
import com.trendyol.checkout.models.dto.request.VasItemRequest;
import com.trendyol.checkout.models.entity.cart.Cart;
import com.trendyol.checkout.models.entity.item.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CartItemItemValidatorTest {

    private Cart cart;
    private ItemValidator validator;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        validator = new ItemValidator(cart);
    }


    @Test
    void it_should_throw_illegal_argument_exception_when_empty_request_field_given() {
        // given
        ItemRequest request = ItemRequest.builder().itemId(3).sellerId(1000).categoryId(1000).price(200.0).quantity(9).build();

        // when
        // then
        assertDoesNotThrow(() -> validator.validateNonVasItemRequest(request));
    }

    @Test
    void it_should_succeed_when_cart_has_less_than_30_items_10_unique_items_and_total_price_not_greater_than_500_000() {
        // given
        DefaultItem defaultItem1 = DefaultItem.builder().itemId(1).sellerId(1000).categoryId(1000).price(100).quantity(8).type(ItemType.DEFAULT_ITEM).build();
        DefaultItem defaultItem2 = DefaultItem.builder().itemId(2).sellerId(1000).categoryId(1000).price(200).quantity(5).type(ItemType.DEFAULT_ITEM).build();
        cart.setItems(List.of(defaultItem1, defaultItem2));

        ItemRequest request = ItemRequest.builder().itemId(null).sellerId(null).categoryId(null).price(null).quantity(null).build();

        // when
        // then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.validateNonVasItemRequest(request);
        });

        String expectedErrorMessage = "Invalid request parameter(s). Fail reason: " + List.of("Item ID is required", "Category ID is required", "Seller ID is required", "Price must be greater than 0", "Quantity must be greater than 0");
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    void it_should_throw_invalid_cart_state_exception_when_cart_has_more_than_30_items() {
        // given
        VasItem vasItem1 = VasItem.builder().itemId(1).sellerId(1234).categoryId(1111).price(200).quantity(10).type(ItemType.VAS_ITEM).build();
        DefaultItem defaultItem1 = DefaultItem.builder().itemId(2).sellerId(1234).categoryId(1111).price(100).quantity(10).type(ItemType.DEFAULT_ITEM).build();
        DefaultItem defaultItem2 = DefaultItem.builder().itemId(3).sellerId(1234).categoryId(1111).price(200).quantity(10).type(ItemType.DEFAULT_ITEM).vasItems(List.of(vasItem1)).build();
        cart.setItems(List.of(defaultItem1, defaultItem2, vasItem1));

        ItemRequest request = ItemRequest.builder().itemId(4).sellerId(1234).categoryId(1111).price(200.0).quantity(10).build();

        // when
        // then
        InvalidCartStateException exception = assertThrows(InvalidCartStateException.class, () -> {
            validator.validateNonVasItemRequest(request);
        });

        String expectedErrorMessage = "There can be maximum 30 items in the cart";
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    void it_should_throw_invalid_cart_state_exception_when_cart_has_more_than_10_unique_items() {
        // given
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            items.add(DefaultItem.builder().itemId(i).sellerId(1234).categoryId(1111).price(100).quantity(1).type(ItemType.DEFAULT_ITEM).build());
        }
        cart.setItems(items);
        ItemRequest request = ItemRequest.builder().itemId(100).sellerId(1234).categoryId(1111).price(100.0).quantity(1).build();


        // when
        // then
        InvalidCartStateException exception = assertThrows(InvalidCartStateException.class, () -> {
            validator.validateNonVasItemRequest(request);
        });

        String expectedErrorMessage = "There can be maximum 10 unique items in the cart";
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    void it_should_succeed_when_cart_has_more_than_10_unique_vas_items() {
        // given
        List<Item> items = new ArrayList<>();
        items.add(DefaultItem.builder().itemId(111).sellerId(1234).categoryId(1001).price(100).quantity(1).type(ItemType.DEFAULT_ITEM).build());
        for (int i = 0; i < 15; i++) {
            items.add(VasItem.builder().itemId(i).sellerId(5003).categoryId(3242).price(100).quantity(1).type(ItemType.VAS_ITEM).build());
        }
        cart.setItems(items);
        VasItemRequest request = VasItemRequest.builder().itemId(111).categoryId(3242).sellerId(5003).quantity(1).price(100.0).vasItemId(15).build();

        // when
        // then
        assertDoesNotThrow(() -> validator.validateVasItemRequest(request));
    }

    @Test
    void it_should_throw_invalid_cart_state_exception_when_cart_total_price_more_than_500_000() {
        // given
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            items.add(DefaultItem.builder().itemId(i).sellerId(1234).categoryId(1111).price(50000).quantity(4).type(ItemType.DEFAULT_ITEM).build());
        }
        cart.setItems(items);
        ItemRequest request = ItemRequest.builder().itemId(100).sellerId(1234).categoryId(1111).price(100.0).quantity(1).build();

        // when
        // then
        InvalidCartStateException exception = assertThrows(InvalidCartStateException.class, () -> {
            validator.validateNonVasItemRequest(request);
        });

        String expectedErrorMessage = "Cart total can be maximum 500000";
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    void it_should_throw_invalid_cart_state_exception_when_digital_item_is_added_to_default_item_cart() {
        // given
        DefaultItem defaultItem = DefaultItem.builder().itemId(1).sellerId(1234).categoryId(1111).price(100).quantity(1).type(ItemType.DEFAULT_ITEM).build();
        cart.setItems(List.of(defaultItem));

        ItemRequest request = ItemRequest.builder().itemId(2).sellerId(2000).categoryId(7889).price(100.0).quantity(1).build();

        // when
        // then
        InvalidCartStateException exception = assertThrows(InvalidCartStateException.class, () -> {
            validator.validateNonVasItemRequest(request);
        });

        String expectedErrorMessage = "Cart cannot hold both digital and default items. Current cart is holding default items.";
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    void it_should_throw_invalid_cart_state_exception_when_default_item_is_added_to_digital_item_cart() {
        // given
        DigitalItem digitalItem = DigitalItem.builder().itemId(2).sellerId(2000).categoryId(7889).price(100).quantity(1).type(ItemType.DIGITAL_ITEM).build();
        cart.setItems(List.of(digitalItem));

        ItemRequest request = ItemRequest.builder().itemId(1).sellerId(1234).categoryId(1111).price(100.0).quantity(1).build();

        // when
        // then
        InvalidCartStateException exception = assertThrows(InvalidCartStateException.class, () -> {
            validator.validateNonVasItemRequest(request);
        });

        String expectedErrorMessage = "Cart cannot hold both digital and default items. Current cart is holding digital items.";
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    /*
     * validateItemCount
     */

    @Test
    void it_should_succeed_when_individual_digital_item_amount_in_cart_is_not_more_than_5() {
        // given
        ItemRequest request = ItemRequest.builder().itemId(1).sellerId(1234).categoryId(7889).price(10.0).quantity(2).build();

        // when
        // then
        assertDoesNotThrow(() -> validator.validateNonVasItemRequest(request));
    }

    @Test
    void it_should_throw_invalid_cart_state_exception_when_individual_digital_item_amount_in_cart_is_more_than_5() {
        // given
        ItemRequest request = ItemRequest.builder().itemId(1).sellerId(1234).categoryId(7889).price(10.0).quantity(26).build();

        // when
        // then
        InvalidCartStateException exception = assertThrows(InvalidCartStateException.class, () -> {
            validator.validateNonVasItemRequest(request);
        });

        String expectedErrorMessage = "Each digital_item can be added maximum of 5 times to the cart.";
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    void it_should_succeed_when_individual_vas_item_amount_in_cart_is_not_more_than_10() {
        // given
        DefaultItem defaultItem = DefaultItem.builder().itemId(1).categoryId(1001).sellerId(1234).price(1200).quantity(1).type(ItemType.DEFAULT_ITEM).build();
        cart.setItems(List.of(defaultItem));

        VasItemRequest request = VasItemRequest.builder().itemId(1).categoryId(3242).sellerId(5003).quantity(3).price(100.0).vasItemId(15).build();

        // when
        // then
        assertDoesNotThrow(() -> validator.validateVasItemRequest(request));
    }

    @Test
    void it_should_succeed_when_individual_default_item_amount_in_cart_is_not_more_than_10() {
        // given
        ItemRequest request = ItemRequest.builder().itemId(1).sellerId(1000).categoryId(1000).price(100.0).quantity(8).build();

        // when
        // then
        assertDoesNotThrow(() -> validator.validateNonVasItemRequest(request));
    }

    @Test
    void it_should_throw_invalid_cart_state_exception_when_individual_default_item_amount_in_cart_is_more_than_10() {
        // given
        ItemRequest request = ItemRequest.builder().itemId(1).sellerId(1000).categoryId(1000).price(100.0).quantity(15).build();

        // when
        // then
        InvalidCartStateException exception = assertThrows(InvalidCartStateException.class, () -> {
            validator.validateNonVasItemRequest(request);
        });

        String expectedErrorMessage = "Each default_item can be added maximum of 10 times to the cart.";
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    /*
     * validateNonVasItemCategoryAndSeller
     */

    @Test
    void it_should_succeed_when_valid_non_vas_item_category_id_and_seller_id_passed() {
        // given
        int categoryId = 1234;
        int sellerId = 1234;
        ItemRequest request = ItemRequest.builder().itemId(1).sellerId(sellerId).categoryId(categoryId).price(100.0).quantity(8).build();

        // when
        // then
        assertDoesNotThrow(() -> validator.validateNonVasItemRequest(request));
    }

    @Test
    void it_should_throw_invalid_cart_item_exception_when_invalid_non_vas_item_category_id_passed() {
        // given
        int categoryId = 3242;
        int sellerId = 1234;
        ItemRequest request = ItemRequest.builder().itemId(1).sellerId(sellerId).categoryId(categoryId).price(100.0).quantity(8).build();

        // when
        // then
        InvalidCartItemException exception = assertThrows(InvalidCartItemException.class, () -> {
            validator.validateNonVasItemRequest(request);
        });

        String expectedErrorMessage = "Cannot add item with categoryId: " + categoryId + ". This categoryId is only for VasItems";
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    void it_should_throw_invalid_cart_item_exception_when_invalid_non_vas_item_seller_id_passed() {
        // given
        int categoryId = 1234;
        int sellerId = 5003;
        ItemRequest request = ItemRequest.builder().itemId(1).sellerId(sellerId).categoryId(categoryId).price(100.0).quantity(8).build();

        // when
        // then
        InvalidCartItemException exception = assertThrows(InvalidCartItemException.class, () -> {
            validator.validateNonVasItemRequest(request);
        });

        String expectedErrorMessage = "Cannot add item with sellerId: " + sellerId + ". This sellerId is only for VasItems";
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    /*
     * validateVasItemCategoryAndSeller
     */

    @Test
    void it_should_succeed_when_valid_vas_item_category_id_and_seller_id_passed() {
        // given
        int categoryId = 3242;
        int sellerId = 5003;
        DefaultItem defaultItem = DefaultItem.builder().itemId(1).categoryId(1001).sellerId(1234).price(1200).quantity(1).type(ItemType.DEFAULT_ITEM).build();
        cart.setItems(List.of(defaultItem));

        VasItemRequest request = VasItemRequest.builder().itemId(1).categoryId(categoryId).sellerId(sellerId).quantity(3).price(100.0).vasItemId(15).build();

        // when
        // then
        assertDoesNotThrow(() -> validator.validateVasItemRequest(request));
    }

    @Test
    void it_should_throw_invalid_cart_item_exception_when_invalid_vas_item_category_id_passed() {
        // given
        int categoryId = 1000;
        int sellerId = 5003;
        DefaultItem defaultItem = DefaultItem.builder().itemId(1).categoryId(1001).sellerId(1234).price(1200).quantity(1).type(ItemType.DEFAULT_ITEM).build();
        cart.setItems(List.of(defaultItem));

        VasItemRequest request = VasItemRequest.builder().itemId(1).categoryId(categoryId).sellerId(sellerId).quantity(3).price(100.0).vasItemId(15).build();

        // when
        // then
        InvalidCartItemException exception = assertThrows(InvalidCartItemException.class, () -> {
            validator.validateVasItemRequest(request);
        });

        String expectedErrorMessage = "Cannot add VasItem with categoryId: " + categoryId + ". VasItems should have categoryId of 3242";
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    void it_should_throw_invalid_cart_item_exception_when_invalid_vas_item_seller_id_passed() {
        // given
        int categoryId = 3242;
        int sellerId = 1000;
        DefaultItem defaultItem = DefaultItem.builder().itemId(1).categoryId(1001).sellerId(1234).price(1200).quantity(1).type(ItemType.DEFAULT_ITEM).build();
        cart.setItems(List.of(defaultItem));

        VasItemRequest request = VasItemRequest.builder().itemId(1).categoryId(categoryId).sellerId(sellerId).quantity(3).price(100.0).vasItemId(15).build();

        // when
        // then
        InvalidCartItemException exception = assertThrows(InvalidCartItemException.class, () -> {
            validator.validateVasItemRequest(request);
        });

        String expectedErrorMessage = "Cannot add item with sellerId: " + sellerId + ". VasItems should have sellerId of 5003";
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    /*
     *   validateDefaultItemOfVasItem
     */
    @Test
    void it_should_succeed_when_default_item_of_vas_item_is_valid() {
        // given
        DefaultItem defaultItem = DefaultItem.builder().itemId(1).categoryId(1001).sellerId(1001).price(100).quantity(2).type(ItemType.DEFAULT_ITEM).vasItems(new ArrayList<>()).build();
        List<Item> cartItems = List.of(defaultItem);
        VasItemRequest request = VasItemRequest.builder().itemId(1).vasItemId(2).sellerId(5003).categoryId(3242).quantity(2).price(100.0).build();
        cart.setItems(cartItems);
        // when
        // then
        assertDoesNotThrow(() -> validator.validateVasItemRequest(request));
    }

    @Test
    void it_should_throw_invalid_cart_item_exception_when_no_default_item_with_id_in_cart() {
        // given
        VasItemRequest request = VasItemRequest.builder().itemId(1).vasItemId(2).sellerId(5003).categoryId(3242).quantity(2).price(100.0).build();

        // when
        // then
        InvalidCartItemException exception = assertThrows(InvalidCartItemException.class, () -> {
            validator.validateVasItemRequest(request);
        });

        String expectedErrorMessage = "There is no default item in your cart with the given id: " + request.getItemId();
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    void it_should_throw_invalid_cart_item_exception_when_default_item_has_invalid_category_id() {
        // given
        DefaultItem defaultItem = DefaultItem.builder().itemId(1).categoryId(9999).sellerId(1001).price(100).quantity(2).type(ItemType.DEFAULT_ITEM).vasItems(new ArrayList<>()).build();
        VasItemRequest request = VasItemRequest.builder().itemId(1).vasItemId(2).sellerId(5003).categoryId(3242).quantity(2).price(100.0).build();
        List<Item> cartItems = List.of(defaultItem);
        cart.setItems(cartItems);
        // when
        // then
        InvalidCartItemException exception = assertThrows(InvalidCartItemException.class, () -> {
            validator.validateVasItemRequest(request);
        });

        String expectedErrorMessage = "You can only add vasItem to default items with category ids: " + List.of(1001, 3004);
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    void it_should_throw_invalid_cart_state_exception_when_more_than_3_vas_items_are_added_to_default_item() {
        // given
        DefaultItem defaultItem = DefaultItem.builder().itemId(1).categoryId(1001).sellerId(1001).price(100).quantity(2).type(ItemType.DEFAULT_ITEM).vasItems(new ArrayList<>()).build();
        VasItemRequest request = VasItemRequest.builder().itemId(1).vasItemId(2).sellerId(5003).categoryId(3242).quantity(5).price(100.0).build();
        List<Item> cartItems = List.of(defaultItem);
        cart.setItems(cartItems);

        // when
        // then
        InvalidCartStateException exception = assertThrows(InvalidCartStateException.class, () -> {
            validator.validateVasItemRequest(request);
        });

        String expectedErrorMessage = "You cannot add more than 3 VasItem to default items";
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    void it_should_throw_invalid_cart_item_exception_when_vas_item_price_more_than_vas_item() {
        // given
        DefaultItem defaultItem = DefaultItem.builder().itemId(1).categoryId(1001).sellerId(1001).price(10).quantity(2).type(ItemType.DEFAULT_ITEM).vasItems(new ArrayList<>()).build();
        VasItemRequest request = VasItemRequest.builder().itemId(1).vasItemId(2).sellerId(5003).categoryId(3242).quantity(2).price(1000.0).build();
        List<Item> cartItems = List.of(defaultItem);
        cart.setItems(cartItems);

        // when
        // then
        InvalidCartItemException exception = assertThrows(InvalidCartItemException.class, () -> {
            validator.validateVasItemRequest(request);
        });

        String expectedErrorMessage = "VasItem price cannot be more than default item price. VasItem price: " + request.getPrice() + ", Default item price: " + defaultItem.getPrice();
        assertEquals(expectedErrorMessage, exception.getMessage());
    }
}