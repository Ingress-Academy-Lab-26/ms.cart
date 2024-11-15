package org.ingress.cartms.mapper;

import org.ingress.cartms.dao.entity.CartEntity;
import org.ingress.cartms.model.enums.CartStatus;
import org.ingress.cartms.model.request.CartRequest;
import org.ingress.cartms.model.response.UserCartsResponse;

public enum CartMapper {
    CART_MAPPER;
    public static CartEntity toEntity(CartRequest dto) {
        return CartEntity.builder()
                .buyerId(dto.getBuyerId())
                .productId(dto.getProductId())
                .quantity(dto.getQuantity())
                .status(CartStatus.ACTIVE)
                .build();
    }

    public static CartRequest toDto(CartEntity entity) {
        return CartRequest.builder()
                .buyerId(entity.getBuyerId())
                .productId(entity.getProductId())
                .quantity(entity.getQuantity())
                .build();
    }

    public static UserCartsResponse toUserCartsResponse(CartEntity cartEntity) {
        return UserCartsResponse.builder()
                .supplierId(cartEntity.getSupplierId())
                .productId(cartEntity.getProductId())
                .quantity(cartEntity.getQuantity())
                .status(cartEntity.getStatus())
                .createdAt(cartEntity.getCreatedAt())
                .build();
    }
}
