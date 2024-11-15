package org.ingress.cartms.service.abstracts;

import org.ingress.cartms.dao.entity.CartEntity;


public interface CartCacheService {

    void saveUserCartToCache(Long buyerId, CartEntity cart);

    CartEntity getUserCartFromCache(Long buyerId, Long productId);


    void updateUserCartInCache(Long buyerId, CartEntity cart);


    void deleteUserCartFromCache(Long buyerId, Long productId);

    public void saveCartsToCache();
}

