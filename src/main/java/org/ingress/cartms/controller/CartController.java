package org.ingress.cartms.controller;

import lombok.RequiredArgsConstructor;
import org.ingress.cartms.model.request.CartRequest;
import org.ingress.cartms.model.response.UserCartsResponse;
import org.ingress.cartms.service.abstracts.CartService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;


    @PostMapping
    @ResponseStatus(CREATED)
    public void saveCart(@Valid @RequestBody CartRequest cartRequest) {
        cartService.insertOrUpdateCart(cartRequest);
    }

    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    public void deleteCart(@RequestParam Long buyerId, @RequestParam Long productId) {
        cartService.deleteCart(buyerId, productId);
    }

    @GetMapping
    @ResponseStatus(OK)
    public Map<Long, List<UserCartsResponse>> getCartsByUserId(@RequestParam Long buyerId) {
        return cartService.getCartsByUserId(buyerId);
    }

}