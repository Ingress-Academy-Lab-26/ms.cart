package org.ingress.cartms.dao.repository;

import org.ingress.cartms.dao.entity.CartEntity;
import org.ingress.cartms.model.enums.CartStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends CrudRepository<CartEntity, Long> {
    Optional<CartEntity> findByBuyerIdAndProductIdAndStatus(Long buyerId, Long productId, CartStatus status);
    List<CartEntity> findByBuyerId(Long buyerId , CartStatus status);

    List<CartEntity> findByBuyerIdAndStatusOrderByCreatedAtDesc(Long buyerId, CartStatus status);
}
