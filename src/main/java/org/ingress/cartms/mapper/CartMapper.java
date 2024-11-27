package org.ingress.cartms.mapper;

import org.ingress.cartms.dao.entity.CartEntity;
import org.ingress.cartms.model.enums.CartStatus;
import org.ingress.cartms.model.request.CartRequest;
import org.ingress.cartms.model.response.UserCarts;

public enum CartMapper {
    CART_MAPPER;
    public CartEntity toEntity(CartRequest dto) {
        return CartEntity.builder()
                .buyerId(dto.getBuyerId())
                .productId(dto.getProductId())
                .quantity(dto.getQuantity())
                .status(CartStatus.ACTIVE)
                .build();
    }

    public UserCarts toUserCartsResponse(CartEntity cartEntity) {
        return UserCarts.builder()
                .supplierId(cartEntity.getSupplierId())
                .productId(cartEntity.getProductId())
                .quantity(cartEntity.getQuantity())
                .createdAt(cartEntity.getCreatedAt())
                .build();
    }
}
