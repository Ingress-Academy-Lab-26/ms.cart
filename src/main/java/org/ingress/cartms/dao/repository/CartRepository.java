package org.ingress.cartms.dao.repository;

import org.ingress.cartms.dao.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
    Optional<CartEntity> findByBuyerIdAndProductId(Long buyerId, Long productId);
    List<CartEntity> findByBuyerId(Long buyerId);
}
