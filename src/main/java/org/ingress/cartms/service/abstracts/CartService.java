package org.ingress.cartms.service.abstracts;

import org.ingress.cartms.model.request.CartRequest;
import org.ingress.cartms.model.response.UserCartsResponse;

import java.util.List;
import java.util.Map;

/**
 * Service interface for managing cart operations.
 * Provides methods to insert, update, delete, and retrieve carts for users.
 */
public interface CartService {

    void insertOrUpdateCart(CartRequest cartRequest);
    void deleteCart(Long buyerId, Long productId);
    Map<Long, List<UserCartsResponse>> getCartsByUserId(Long buyerId);
}
