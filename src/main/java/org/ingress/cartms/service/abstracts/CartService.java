package org.ingress.cartms.service.abstracts;

import org.ingress.cartms.model.request.CartRequest;
import org.ingress.cartms.model.response.GroupedCartsBySupplier;

import java.util.List;

public interface CartService {

    void insertOrUpdateCart(CartRequest cartRequest);
    void deleteCart(Long buyerId, Long productId);
    List<GroupedCartsBySupplier> getCartsByUserId(Long buyerId);


}
