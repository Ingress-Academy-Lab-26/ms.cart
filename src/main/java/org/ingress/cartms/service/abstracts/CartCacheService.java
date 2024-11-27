package org.ingress.cartms.service.abstracts;

import org.ingress.cartms.dao.entity.CartEntity;

import java.util.List;
import java.util.Optional;


public interface CartCacheService {

    void saveUserCartToCache(Long buyerId, Long productId, CartEntity cart);

    Optional<CartEntity> getUserCartFromCache(Long buyerId, Long productId);

    void updateUserCartInCache(Long buyerId, CartEntity cart);


    void deleteUserCartFromCache(Long buyerId, Long productId);

    void saveCartsToCache();

    void saveUserCartsToCache(Long buyerId, List<CartEntity> carts);

    List<CartEntity> getUserCartsFromCache(Long buyerId);

    void saveSupplierIdToCache(Long buyerId, Long productId, Long supplierId);

    Optional<Long> getSupplierIdFromCache(Long buyerId, Long productId);
}

