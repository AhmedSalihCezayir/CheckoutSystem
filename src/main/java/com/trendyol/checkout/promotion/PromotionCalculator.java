package com.trendyol.checkout.promotion;

import com.trendyol.checkout.models.entity.item.Item;
import com.trendyol.checkout.models.entity.item.ItemType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class PromotionCalculator {
    private final int SAME_SELLER_PROMOTION_ID = 9909;
    private final int SAME_SELLER_PROMOTION_DISCOUNT_PERCENTAGE = 10;

    private final int CATEGORY_PROMOTION_ID = 5676;
    private final int CATEGORY_PROMOTION_APPLIED_FOR_CATEGORY_ID = 3003;
    private final int CATEGORY_PROMOTION_DISCOUNT_PERCENTAGE = 5;

    private final int TOTAL_PRICE_PROMOTION_ID = 1232;
    private final List<Integer> TOTAL_PRICE_PROMOTION_THRESHOLDS = List.of(5000, 10_000, 50_000);
    private final List<Integer> TOTAL_PRICE_PROMOTION_DISCOUNTS = List.of(250, 500, 1000, 2000);

    public HashMap<String, Number> calculateMaxPromotion(List<Item> cartItems, double totalCartPrice) {
        double maxPromotion = 0;
        Integer idMaxPromotion = null;

        double sameSellerDiscount = calculateSameSellerPromotion(cartItems, totalCartPrice);
        double categoryDiscount = calculateCategoryPromotion(cartItems);
        double totalPriceDiscount = calculateTotalPricePromotion(totalCartPrice);

        if (sameSellerDiscount > maxPromotion) {
            maxPromotion = sameSellerDiscount;
            idMaxPromotion = SAME_SELLER_PROMOTION_ID;
        }
        if (categoryDiscount > maxPromotion) {
            maxPromotion = categoryDiscount;
            idMaxPromotion = CATEGORY_PROMOTION_ID;
        }
        if (totalPriceDiscount > maxPromotion) {
            maxPromotion = totalPriceDiscount;
            idMaxPromotion = TOTAL_PRICE_PROMOTION_ID;
        }
        if (maxPromotion > totalCartPrice) {
            maxPromotion = 0;
            idMaxPromotion = null;
        }

        HashMap<String, Number> bestPromotion = new HashMap<>();
        bestPromotion.put("discount", maxPromotion);
        bestPromotion.put("promotionId", idMaxPromotion);

        return bestPromotion;
    }

    private double calculateSameSellerPromotion(List<Item> cartItems, double totalCartPrice) {
        if (cartItems.size() > 1 && allItemsFromSameSeller(cartItems) ) {
            return totalCartPrice * (SAME_SELLER_PROMOTION_DISCOUNT_PERCENTAGE / 100.0);
        } else {
            return 0.0;
        }
    }

    private double calculateCategoryPromotion(List<Item> cartItems) {
        return  cartItems.stream()
                .filter(i -> i.getCategoryId() == CATEGORY_PROMOTION_APPLIED_FOR_CATEGORY_ID)
                .map(i -> i.getPrice() * i.getQuantity())
                .reduce(0.0, (subtotal, element) -> subtotal + element * (CATEGORY_PROMOTION_DISCOUNT_PERCENTAGE / 100.0));
    }

    private double calculateTotalPricePromotion(double totalCartPrice) {
        for (int i = TOTAL_PRICE_PROMOTION_THRESHOLDS.size() - 1; i >= 0; i--) {
            if (totalCartPrice >= TOTAL_PRICE_PROMOTION_THRESHOLDS.get(i)) {
                return TOTAL_PRICE_PROMOTION_DISCOUNTS.get(i + 1);
            }
        }
        return TOTAL_PRICE_PROMOTION_DISCOUNTS.get(0);
    }

    private boolean allItemsFromSameSeller(List<Item> cartItems) {
        List<Item> nonVasItems = cartItems.stream()
                .filter(i -> i.getType() != ItemType.VAS_ITEM)
                .toList();

        if (nonVasItems.isEmpty()) {
            return false;
        }
        return nonVasItems.stream().allMatch(i -> i.getSellerId() == nonVasItems.get(0).getSellerId());
    }

}
