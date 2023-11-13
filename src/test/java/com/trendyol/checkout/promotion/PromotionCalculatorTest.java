package com.trendyol.checkout.promotion;

import com.trendyol.checkout.models.entity.item.DefaultItem;
import com.trendyol.checkout.models.entity.item.Item;
import com.trendyol.checkout.models.entity.item.ItemType;
import com.trendyol.checkout.models.entity.item.VasItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PromotionCalculatorTest {

    private PromotionCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new PromotionCalculator();
    }

    @Test
    void it_should_return_best_promotion_id_as_null_when_best_promotion_is_more_than_cart_total_price() {
        // given
        VasItem vasItem1 = VasItem.builder().itemId(1).categoryId(3242).sellerId(5003).price(10).quantity(1).type(ItemType.VAS_ITEM).build();
        DefaultItem defaultItem1 = DefaultItem.builder().itemId(2).categoryId(1001).sellerId(100).price(50).quantity(1).type(ItemType.DEFAULT_ITEM).vasItems(List.of(vasItem1)).build();
        DefaultItem defaultItem2 = DefaultItem.builder().itemId(3).categoryId(1001).sellerId(100).price(50).quantity(1).type(ItemType.DEFAULT_ITEM).build();

        List<Item> cartItems = List.of(defaultItem1, defaultItem2, vasItem1);
        int totalCartPrice = 110;

        // when
        HashMap<String, Number> bestPromotion = calculator.calculateMaxPromotion(cartItems, totalCartPrice);

        // then
        assertNull(bestPromotion.get("promotionId"));
    }

    @Test
    void it_should_return_best_promotion_discount_as_0_when_best_promotion_is_more_than_cart_total_price() {
        // given
        VasItem vasItem1 = VasItem.builder().itemId(1).categoryId(3242).sellerId(5003).price(10).quantity(1).type(ItemType.VAS_ITEM).build();
        DefaultItem defaultItem1 = DefaultItem.builder().itemId(2).categoryId(1001).sellerId(100).price(50).quantity(1).type(ItemType.DEFAULT_ITEM).vasItems(List.of(vasItem1)).build();
        DefaultItem defaultItem2 = DefaultItem.builder().itemId(3).categoryId(1001).sellerId(100).price(50).quantity(1).type(ItemType.DEFAULT_ITEM).build();

        List<Item> cartItems = List.of(defaultItem1, defaultItem2, vasItem1);
        int totalCartPrice = 110;

        // when
        HashMap<String, Number> bestPromotion = calculator.calculateMaxPromotion(cartItems, totalCartPrice);

        // then
        assertEquals(0.0, bestPromotion.get("discount"));
    }

    @Test
    void it_should_return_best_promotion_id_as_9909_when_condition_met_for_same_seller_promotion() {
        // given
        DefaultItem defaultItem1 = DefaultItem.builder().itemId(2).categoryId(1001).sellerId(100).price(5000).quantity(1).type(ItemType.DEFAULT_ITEM).build();
        DefaultItem defaultItem2 = DefaultItem.builder().itemId(3).categoryId(1001).sellerId(100).price(5000).quantity(1).type(ItemType.DEFAULT_ITEM).build();
        DefaultItem defaultItem3 = DefaultItem.builder().itemId(4).categoryId(1001).sellerId(100).price(5000).quantity(1).type(ItemType.DEFAULT_ITEM).build();

        List<Item> cartItems = List.of(defaultItem1, defaultItem2, defaultItem3);
        int totalCartPrice = 15000;

        // when
        HashMap<String, Number> bestPromotion = calculator.calculateMaxPromotion(cartItems, totalCartPrice);

        // then
        assertEquals(9909, bestPromotion.get("promotionId"));
    }

    @Test
    void it_should_return_best_promotion_discount_as_1500_when_condition_met_for_same_seller_promotion() {
        // given
        DefaultItem defaultItem1 = DefaultItem.builder().itemId(2).categoryId(1001).sellerId(100).price(5000).quantity(1).type(ItemType.DEFAULT_ITEM).build();
        DefaultItem defaultItem2 = DefaultItem.builder().itemId(3).categoryId(1001).sellerId(100).price(5000).quantity(1).type(ItemType.DEFAULT_ITEM).build();
        DefaultItem defaultItem3 = DefaultItem.builder().itemId(4).categoryId(1001).sellerId(100).price(5000).quantity(1).type(ItemType.DEFAULT_ITEM).build();

        List<Item> cartItems = List.of(defaultItem1, defaultItem2, defaultItem3);
        int totalCartPrice = 15000;

        // when
        HashMap<String, Number> bestPromotion = calculator.calculateMaxPromotion(cartItems, totalCartPrice);

        // then
        assertEquals(1500.0, bestPromotion.get("discount"));
    }

    @Test
    void it_should_return_best_promotion_id_as_5676_when_condition_met_for_category_promotion() {
        // given
        DefaultItem defaultItem1 = DefaultItem.builder().itemId(2).categoryId(3003).sellerId(100).price(10000).quantity(1).type(ItemType.DEFAULT_ITEM).build();
        DefaultItem defaultItem2 = DefaultItem.builder().itemId(3).categoryId(3003).sellerId(101).price(10000).quantity(1).type(ItemType.DEFAULT_ITEM).build();
        DefaultItem defaultItem3 = DefaultItem.builder().itemId(4).categoryId(3003).sellerId(102).price(10000).quantity(1).type(ItemType.DEFAULT_ITEM).build();

        List<Item> cartItems = List.of(defaultItem1, defaultItem2, defaultItem3);
        int totalCartPrice = 30000;

        // when
        HashMap<String, Number> bestPromotion = calculator.calculateMaxPromotion(cartItems, totalCartPrice);

        // then
        assertEquals(5676, bestPromotion.get("promotionId"));
    }

    @Test
    void it_should_return_best_promotion_discount_as_1500_when_condition_met_for_category_promotion() {
        // given
        DefaultItem defaultItem1 = DefaultItem.builder().itemId(2).categoryId(3003).sellerId(100).price(10000).quantity(1).type(ItemType.DEFAULT_ITEM).build();
        DefaultItem defaultItem2 = DefaultItem.builder().itemId(3).categoryId(3003).sellerId(101).price(10000).quantity(1).type(ItemType.DEFAULT_ITEM).build();
        DefaultItem defaultItem3 = DefaultItem.builder().itemId(4).categoryId(3003).sellerId(102).price(10000).quantity(1).type(ItemType.DEFAULT_ITEM).build();

        List<Item> cartItems = List.of(defaultItem1, defaultItem2, defaultItem3);
        int totalCartPrice = 30000;

        // when
        HashMap<String, Number> bestPromotion = calculator.calculateMaxPromotion(cartItems, totalCartPrice);

        // then
        assertEquals(1500.0, bestPromotion.get("discount"));
    }

    @Test
    void it_should_return_best_promotion_id_as_1232_when_condition_met_for_total_price_promotion() {
        // given
        DefaultItem defaultItem1 = DefaultItem.builder().itemId(2).categoryId(1000).sellerId(100).price(10000).quantity(1).type(ItemType.DEFAULT_ITEM).build();
        DefaultItem defaultItem2 = DefaultItem.builder().itemId(3).categoryId(1000).sellerId(101).price(10000).quantity(1).type(ItemType.DEFAULT_ITEM).build();
        DefaultItem defaultItem3 = DefaultItem.builder().itemId(4).categoryId(1000).sellerId(102).price(10000).quantity(1).type(ItemType.DEFAULT_ITEM).build();

        List<Item> cartItems = List.of(defaultItem1, defaultItem2, defaultItem3);
        int totalCartPrice = 30000;

        // when
        HashMap<String, Number> bestPromotion = calculator.calculateMaxPromotion(cartItems, totalCartPrice);

        // then
        assertEquals(1232, bestPromotion.get("promotionId"));
    }

    @Test
    void it_should_return_best_promotion_discount_as_1000_when_condition_met_for_total_price_promotion() {
        // given
        DefaultItem defaultItem1 = DefaultItem.builder().itemId(2).categoryId(1000).sellerId(100).price(10000).quantity(1).type(ItemType.DEFAULT_ITEM).build();
        DefaultItem defaultItem2 = DefaultItem.builder().itemId(3).categoryId(1000).sellerId(101).price(10000).quantity(1).type(ItemType.DEFAULT_ITEM).build();
        DefaultItem defaultItem3 = DefaultItem.builder().itemId(4).categoryId(1000).sellerId(102).price(10000).quantity(1).type(ItemType.DEFAULT_ITEM).build();

        List<Item> cartItems = List.of(defaultItem1, defaultItem2, defaultItem3);
        int totalCartPrice = 30000;

        // when
        HashMap<String, Number> bestPromotion = calculator.calculateMaxPromotion(cartItems, totalCartPrice);

        // then
        assertEquals(1000.0, bestPromotion.get("discount"));
    }

    @Test
    void it_should_not_return_best_promotion_id_as_9909_when_same_seller_vas_items_are_in_cart() {
        // given
        VasItem vasItem1 = VasItem.builder().itemId(1).categoryId(3242).sellerId(5003).price(10000).quantity(1).type(ItemType.VAS_ITEM).build();
        VasItem vasItem2 = VasItem.builder().itemId(2).categoryId(3242).sellerId(5003).price(10000).quantity(1).type(ItemType.VAS_ITEM).build();
        VasItem vasItem3 = VasItem.builder().itemId(3).categoryId(3242).sellerId(5003).price(10000).quantity(1).type(ItemType.VAS_ITEM).build();

        List<Item> cartItems = List.of(vasItem1, vasItem2, vasItem3);
        int totalCartPrice = 30000;

        // when
        HashMap<String, Number> bestPromotion = calculator.calculateMaxPromotion(cartItems, totalCartPrice);

        // then
        assertNotEquals(9909, bestPromotion.get("promotionId"));
    }
}