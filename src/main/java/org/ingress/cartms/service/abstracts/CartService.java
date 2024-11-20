package org.ingress.cartms.service.abstracts;

import org.ingress.cartms.model.request.CartRequest;
import org.ingress.cartms.model.response.UserCartsResponse;

import java.util.List;
import java.util.Map;

public interface CartService {

    void insertOrUpdateCart(CartRequest cartRequest);
    void deleteCart(Long buyerId, Long productId);
    Map<Long, List<UserCartsResponse>> getCartsByUserId(Long buyerId);


}
