package com.trendyol.checkout.service;

import com.trendyol.checkout.exception.ItemNotFoundException;
import com.trendyol.checkout.models.dto.request.ItemRequest;
import com.trendyol.checkout.models.dto.request.VasItemRequest;
import com.trendyol.checkout.models.dto.response.BasicResponse;
import com.trendyol.checkout.models.entity.cart.Cart;
import com.trendyol.checkout.models.entity.item.*;
import com.trendyol.checkout.promotion.PromotionCalculator;
import com.trendyol.checkout.validation.ItemValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CartServiceImplTest {

    @Mock
    private ItemValidator validator;

    @Mock
    private PromotionCalculator promotionCalculator;


    @InjectMocks
    private CartServiceImpl cartService;

    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cartService = new CartServiceImpl(cart, validator, promotionCalculator);
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void clearUp() {
        cart = new Cart();
    }

    /*
     * addNonVasItemToCart
     */
    @Test
    void it_should_successfully_add_non_vas_item_and_return_http_status_created() {
        // given
        ItemRequest request = ItemRequest.builder().itemId(1).sellerId(100).categoryId(100).quantity(1).price(1000.0).build();

        // when
        ResponseEntity<BasicResponse> response = cartService.addNonVasItemToCart(request);

        // then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void it_should_successfully_add_non_vas_item_and_return_message_item_is_successfully_added() {
        // given
        ItemRequest request = ItemRequest.builder().itemId(1).sellerId(100).categoryId(100).quantity(1).price(1000.0).build();

        // when
        ResponseEntity<BasicResponse> response = cartService.addNonVasItemToCart(request);

        // then
        assertEquals("Item with id: 1 successfully added to cart", response.getBody().getMessage());
    }

    @Test
    void it_should_successfully_add_non_vas_item_to_empty_cart_and_return_correct_cart_total_price() {
        // given
        ItemRequest request = ItemRequest.builder().itemId(1).sellerId(100).categoryId(100).quantity(3).price(1000.0).build();

        // when
        // then
        cartService.addNonVasItemToCart(request);
        assertEquals(3000.0, cart.getTotalPrice());
    }

    @Test
    void it_should_successfully_add_different_non_vas_item_to_non_empty_cart_and_return_correct_cart_total_price() {
        // given
        DefaultItem defaultItem = DefaultItem.builder().itemId(1).sellerId(1).categoryId(1).quantity(1).price(100).type(ItemType.DEFAULT_ITEM).build();
        cart.setItems(new ArrayList<>(List.of(defaultItem)));

        ItemRequest request = ItemRequest.builder().itemId(2).sellerId(100).categoryId(100).quantity(3).price(1000.0).build();

        // when
        // then
        cartService.addNonVasItemToCart(request);
        assertEquals(3100.0, cart.getTotalPrice());
    }

    @Test
    void it_should_successfully_add_already_existing_non_vas_item_to_non_empty_cart_and_return_correct_cart_total_price() {
        // given
        DefaultItem defaultItem = DefaultItem.builder().itemId(1).sellerId(1).categoryId(1).quantity(1).price(100).type(ItemType.DEFAULT_ITEM).build();
        cart.setItems(new ArrayList<>(List.of(defaultItem)));

        ItemRequest request = ItemRequest.builder().itemId(1).sellerId(1).categoryId(1).quantity(3).price(100.0).build();

        // when
        // then
        cartService.addNonVasItemToCart(request);
        assertEquals(400.0, cart.getTotalPrice());
    }

    @Test
    void it_should_successfully_add_already_existing_non_vas_item_to_non_empty_cart_and_individual_item_quantity_increase() {
        // given
        DefaultItem defaultItem = DefaultItem.builder().itemId(1).sellerId(1).categoryId(1).quantity(1).price(100).type(ItemType.DEFAULT_ITEM).build();
        cart.setItems(new ArrayList<>(List.of(defaultItem)));

        ItemRequest request = ItemRequest.builder().itemId(1).sellerId(1).categoryId(1).quantity(3).price(100.0).build();

        // when
        // then
        cartService.addNonVasItemToCart(request);
        Item item = cart.getItems().get(0);
        assertEquals(4, item.getQuantity());
    }

    @Test
    void it_should_successfully_add_different_non_vas_item_to_non_empty_cart_and_total_item_count_increase() {
        // given
        DefaultItem defaultItem1 = DefaultItem.builder().itemId(1).sellerId(1).categoryId(1).quantity(1).price(100).type(ItemType.DEFAULT_ITEM).build();
        DefaultItem defaultItem2 = DefaultItem.builder().itemId(2).sellerId(1).categoryId(1).quantity(4).price(100).type(ItemType.DEFAULT_ITEM).build();
        cart.setItems(new ArrayList<>(List.of(defaultItem1, defaultItem2)));

        ItemRequest request = ItemRequest.builder().itemId(3).sellerId(1).categoryId(1).quantity(3).price(100.0).build();

        // when
        // then
        cartService.addNonVasItemToCart(request);

        assertEquals(3, cart.getItems().size());
    }

    /*
     * addVasItemToCart
     */

    @Test
    void it_should_successfully_add_vas_item_and_return_http_status_created() {
        // given
        DefaultItem defaultItem = DefaultItem.builder().itemId(1).sellerId(1).categoryId(1).quantity(1).price(100).type(ItemType.DEFAULT_ITEM).vasItems(new ArrayList<>()).build();
        cart.setItems(new ArrayList<>(List.of(defaultItem)));

        VasItemRequest request = VasItemRequest.builder().itemId(1).sellerId(5003).categoryId(3242).quantity(1).price(10.0).vasItemId(12).build();

        // when
        ResponseEntity<BasicResponse> response = cartService.addVasItemToCart(request);

        // then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void it_should_successfully_add_vas_item_and_return_message_item_is_successfully_added() {
        // given
        DefaultItem defaultItem = DefaultItem.builder().itemId(1).sellerId(1).categoryId(1).quantity(1).price(100).type(ItemType.DEFAULT_ITEM).vasItems(new ArrayList<>()).build();
        cart.setItems(new ArrayList<>(List.of(defaultItem)));

        VasItemRequest request = VasItemRequest.builder().itemId(1).sellerId(5003).categoryId(3242).quantity(1).price(10.0).vasItemId(12).build();

        // when
        ResponseEntity<BasicResponse> response = cartService.addVasItemToCart(request);

        // then
        assertEquals("Item with id: 12 successfully added to cart", response.getBody().getMessage());
    }

    @Test
    void it_should_successfully_add_vas_item_to_cart_with_single_default_item_and_return_correct_cart_total_price() {
        // given
        DefaultItem defaultItem = DefaultItem.builder().itemId(1).sellerId(1).categoryId(1).quantity(1).price(100).type(ItemType.DEFAULT_ITEM).vasItems(new ArrayList<>()).build();
        cart.setItems(new ArrayList<>(List.of(defaultItem)));

        VasItemRequest request = VasItemRequest.builder().itemId(1).sellerId(5003).categoryId(3242).quantity(1).price(10.0).vasItemId(12).build();

        // when
        // then
        cartService.addVasItemToCart(request);
        assertEquals(110.0, cart.getTotalPrice());
    }

    @Test
    void it_should_successfully_add_already_existing_vas_item_to_non_empty_cart_and_return_correct_cart_total_price() {
        // given
        VasItem vasItem = VasItem.builder().itemId(2).sellerId(5003).categoryId(3242).quantity(1).price(100).type(ItemType.VAS_ITEM).build();
        DefaultItem defaultItem = DefaultItem.builder().itemId(1).sellerId(1).categoryId(1).quantity(1).price(1000).type(ItemType.DEFAULT_ITEM).vasItems(new ArrayList<>(List.of(vasItem))).build();
        cart.setItems(new ArrayList<>(List.of(defaultItem, vasItem)));

        VasItemRequest request = VasItemRequest.builder().itemId(1).sellerId(5003).categoryId(3242).quantity(1).price(10.0).vasItemId(2).build();

        // when
        // then
        cartService.addVasItemToCart(request);
        assertEquals(1110.0, cart.getTotalPrice());
    }

    @Test
    void it_should_successfully_add_already_existing_vas_item_to_non_empty_cart_and_return_correct_individual_item_count() {
        // given
        VasItem vasItem = VasItem.builder().itemId(2).sellerId(5003).categoryId(3242).quantity(1).price(100).type(ItemType.VAS_ITEM).build();
        DefaultItem defaultItem = DefaultItem.builder().itemId(1).sellerId(1).categoryId(1).quantity(1).price(1000).type(ItemType.DEFAULT_ITEM).vasItems(new ArrayList<>()).build();
        cart.setItems(new ArrayList<>(List.of(defaultItem, vasItem)));

        VasItemRequest request = VasItemRequest.builder().itemId(1).sellerId(5003).categoryId(3242).quantity(1).price(10.0).vasItemId(2).build();

        // when
        // then
        cartService.addVasItemToCart(request);
        int itemCount = cart.getItems().stream().filter(i -> i.getItemId() == 2).findFirst().get().getQuantity();
        assertEquals(2, itemCount);
    }

    /*
     * removeItem
     */

    @Test
    void it_should_successfully_remove_non_vas_item_and_give_correct_individual_item_count() {
        // given
        VasItem vasItem = VasItem.builder().itemId(12).sellerId(5003).categoryId(3242).quantity(7).price(100).type(ItemType.VAS_ITEM).build();
        DefaultItem defaultItem1 = DefaultItem.builder().itemId(1).sellerId(1).categoryId(1).quantity(1).price(1000).type(ItemType.DEFAULT_ITEM).vasItems(new ArrayList<>(List.of(vasItem))).build();
        DefaultItem defaultItem2 = DefaultItem.builder().itemId(2).sellerId(1).categoryId(1).quantity(5).price(1000).type(ItemType.DEFAULT_ITEM).vasItems(new ArrayList<>()).build();
        cart.setItems(new ArrayList<>(List.of(defaultItem1, defaultItem2, vasItem)));

        // when
        // then
        cartService.removeItem(1);

        assertEquals(5, cart.getItemCount());
    }

    @Test
    void it_should_successfully_remove_vas_items_of_default_items_when_default_item_is_deleted() {
        // given
        DefaultItem defaultItem1 = DefaultItem.builder().itemId(1).sellerId(1).categoryId(1).quantity(1).price(1000).type(ItemType.DEFAULT_ITEM).vasItems(new ArrayList<>()).build();
        DefaultItem defaultItem2 = DefaultItem.builder().itemId(2).sellerId(1).categoryId(1).quantity(5).price(1000).type(ItemType.DEFAULT_ITEM).vasItems(new ArrayList<>()).build();
        cart.setItems(new ArrayList<>(List.of(defaultItem1, defaultItem2)));

        // when
        // then
        cartService.removeItem(1);
        Item remainingItem = cart.getItems().get(0);

        assertEquals(5, remainingItem.getQuantity());
    }

    @Test
    void it_should_throw_item_not_found_exception_when_no_item_with_id_in_cart() {
        // given
        DefaultItem defaultItem1 = DefaultItem.builder().itemId(1).sellerId(1).categoryId(1).quantity(1).price(1000).type(ItemType.DEFAULT_ITEM).vasItems(new ArrayList<>()).build();
        cart.setItems(new ArrayList<>(List.of(defaultItem1)));

        // when
        // then
        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class, () -> {
            cartService.removeItem(125);
        });

        assertEquals("There is no item with id: 125 in your cart", exception.getMessage());
    }

    @Test
    void it_should_successfully_nullify_cart_type_when_last_digital_item_is_removed() {
        // given
        DigitalItem digitalItem = DigitalItem.builder().itemId(1).sellerId(1).categoryId(1).quantity(1).price(1000).type(ItemType.DIGITAL_ITEM).build();
        cart.setDigitalItemCart(true);
        cart.setItems(new ArrayList<>(List.of(digitalItem)));

        // when
        // then
        cartService.removeItem(1);
        assertNull(cart.isDigitalItemCart());
    }

    @Test
    void it_should_successfully_nullify_cart_type_when_last_default_item_is_removed() {
        // given
        DefaultItem defaultItem = DefaultItem.builder().itemId(1).sellerId(1).categoryId(1).quantity(1).price(1000).type(ItemType.DEFAULT_ITEM).vasItems(new ArrayList<>()).build();
        cart.setDigitalItemCart(true);
        cart.setItems(new ArrayList<>(List.of(defaultItem)));

        // when
        // then
        cartService.removeItem(1);
        assertNull(cart.isDigitalItemCart());
    }

    /*
     * resetCart
     */

    @Test
    void it_should_reset_item_count_to_zero_when_cart_is_reset() {
        // given
        DefaultItem defaultItem = DefaultItem.builder().itemId(1).sellerId(1).categoryId(1).quantity(1).price(1000).type(ItemType.DEFAULT_ITEM).vasItems(new ArrayList<>()).build();
        cart.setItems(new ArrayList<>(List.of(defaultItem)));

        // when
        // then
        cartService.resetCart();
        assertEquals(0, cart.getItemCount());
    }
}