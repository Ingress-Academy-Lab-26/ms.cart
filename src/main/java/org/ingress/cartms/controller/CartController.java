package org.ingress.cartms.controller;

import lombok.RequiredArgsConstructor;
import org.ingress.cartms.model.request.CartRequest;
import org.ingress.cartms.model.response.GroupedCartsBySupplier;
import org.ingress.cartms.service.abstracts.CartService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;


import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;


    @PostMapping
    @ResponseStatus(CREATED)
    @PreAuthorize("@authService.verifyToken(#accessToken)")
    public void saveCart(@Valid @RequestBody CartRequest cartRequest, @RequestHeader(AUTHORIZATION) String accessToken) {
        cartService.insertOrUpdateCart(cartRequest);
    }

    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("@authService.verifyToken(#accessToken)")
    public void deleteCart(@RequestParam Long buyerId, @RequestParam Long productId, @RequestHeader(AUTHORIZATION) String accessToken) {
        cartService.deleteCart(buyerId, productId);
    }

    @GetMapping
    @ResponseStatus(OK)
    @PreAuthorize("@authService.verifyToken(#accessToken)")
    public List<GroupedCartsBySupplier> getCartsByUserId(@RequestParam Long buyerId, @RequestHeader(AUTHORIZATION) String accessToken) {
        return cartService.getCartsByUserId(buyerId);
    }

}