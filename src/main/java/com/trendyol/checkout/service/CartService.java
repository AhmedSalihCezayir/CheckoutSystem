package com.trendyol.checkout.service;

import com.trendyol.checkout.models.dto.request.ItemRequest;
import com.trendyol.checkout.models.dto.request.VasItemRequest;
import com.trendyol.checkout.models.dto.response.BasicResponse;
import org.springframework.http.ResponseEntity;

public interface CartService {
    ResponseEntity<BasicResponse> addNonVasItemToCart(ItemRequest request);
    ResponseEntity<BasicResponse> addVasItemToCart(VasItemRequest request);
    ResponseEntity<BasicResponse> removeItem(int itemId);
    ResponseEntity<BasicResponse> resetCart();
    ResponseEntity<BasicResponse> displayCart();
}
