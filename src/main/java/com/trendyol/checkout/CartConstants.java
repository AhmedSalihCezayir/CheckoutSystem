package com.trendyol.checkout;

import java.util.List;

public final class CartConstants {
    public static final int MAX_UNIQUE_ITEM_TYPE_COUNT_IN_CART = 10;
    public static final int MAX_TOTAL_ITEM_COUNT_IN_CART = 30;
    public static final int MAX_CART_TOTAL_PRICE = 500_000;

    // Default items
    public static final int DEFAULT_ITEM_MAX_COUNT_IN_CART = 10;

    // Digital items
    public static final int DIGITAL_ITEM_CATEGORY_ID = 7889;
    public static final int DIGITAL_ITEM_MAX_COUNT_IN_CART = 5;

    // Vas items
    public static final int VAS_ITEM_CATEGORY_ID = 3242;
    public static final int VAS_ITEM_MAX_COUNT_IN_CART = 10;
    public static final int VAS_ITEM_SELLER_ID = 5003;
    public static final List<Integer> CATEGORY_IDS_FOR_DEFAULT_ITEMS_THAT_CAN_HAVE_VAS_ITEM = List.of(1001, 3004);
    public static final int MAX_VAS_ITEM_COUNT_FOR_A_DEFAULT_ITEM = 3;
}
