package com.trendyol.checkout.controller;

import com.trendyol.checkout.models.dto.request.ItemRequest;
import com.trendyol.checkout.models.dto.request.VasItemRequest;
import com.trendyol.checkout.models.dto.response.BasicResponse;
import com.trendyol.checkout.service.CartServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
    private CartServiceImpl cartService;

    @Autowired
    public CartController(CartServiceImpl cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/items")
    public ResponseEntity<BasicResponse> add(@RequestBody ItemRequest request) {
        return cartService.addNonVasItemToCart(request);
    }

    @PostMapping("/vas_items")
    public ResponseEntity<BasicResponse> add(@RequestBody VasItemRequest request) {
        return cartService.addVasItemToCart(request);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<BasicResponse> delete(@PathVariable int itemId) {
        return cartService.removeItem(itemId);
    }

    @DeleteMapping
    public ResponseEntity<BasicResponse> resetCart() {
        return cartService.resetCart();
    }

    @GetMapping
    public ResponseEntity<BasicResponse> displayCart() {
        return cartService.displayCart();
    }
}
