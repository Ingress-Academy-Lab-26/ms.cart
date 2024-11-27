package org.ingress.cartms.service.concrete;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ingress.cartms.annotation.Loggable;
import org.ingress.cartms.client.ProductClient;
import org.ingress.cartms.dao.entity.CartEntity;
import org.ingress.cartms.dao.repository.CartRepository;
import org.ingress.cartms.exception.NotFoundException;
import org.ingress.cartms.model.enums.CartStatus;
import org.ingress.cartms.model.request.CartRequest;
import org.ingress.cartms.model.response.GroupedCartsBySupplier;
import org.ingress.cartms.model.response.UserCarts;
import org.ingress.cartms.service.abstracts.CartCacheService;
import org.ingress.cartms.service.abstracts.CartService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static org.ingress.cartms.exception.ExceptionConstraints.CART_NOT_FOUND_CODE;
import static org.ingress.cartms.exception.ExceptionConstraints.CART_NOT_FOUND_MESSAGE;
import static org.ingress.cartms.mapper.CartMapper.CART_MAPPER;


@Loggable
@Slf4j
@Service
@RequiredArgsConstructor

public class CartServiceHandler implements CartService {

    private final CartRepository cartRepository;
    private final ProductClient productClient;
    private final CartCacheService cartCacheService;


    @Override
    public void insertOrUpdateCart(CartRequest cartRequest) {

        var cartEntity = fetchOrBuildCart(cartRequest);

        Long supplierId = cartCacheService.getSupplierIdFromCache(cartRequest.getBuyerId(), cartRequest.getProductId())
                .orElseGet(() -> {
                    Long fetchedSupplierId = productClient.getSupplierByProductId(cartEntity.getProductId());
                    cartCacheService.saveSupplierIdToCache(cartRequest.getBuyerId(), cartRequest.getProductId(), fetchedSupplierId);
                    return fetchedSupplierId;
                });

        cartEntity.setSupplierId(supplierId);

        cartRepository.save(cartEntity);

        cartCacheService.saveUserCartToCache(cartRequest.getBuyerId(),cartRequest.getProductId(), cartEntity);

    }

    private CartEntity fetchOrBuildCart(CartRequest cartRequest) {

        return getCartFromCacheOrDatabase(cartRequest)
                .map(existingCart -> updateExistingCart(existingCart, cartRequest))
                .orElseGet(() -> buildNewCart(cartRequest));
    }

    private Optional<CartEntity> getCartFromCacheOrDatabase(CartRequest cartRequest) {
        return cartCacheService.getUserCartFromCache(cartRequest.getBuyerId(), cartRequest.getProductId())
                .or(() -> cartRepository.findByBuyerIdAndProductIdAndStatus(cartRequest.getBuyerId(), cartRequest.getProductId(), CartStatus.ACTIVE));
    }


    @Override
    public void deleteCart(Long buyerId, Long productId) {

        var cart = fetchCartIfExist(buyerId, productId);
        cart.setStatus(CartStatus.DELETED);
        cartRepository.save(cart);

        cartCacheService.deleteUserCartFromCache(buyerId, productId);

    }
    @Override
    public List<GroupedCartsBySupplier> getCartsByUserId(Long buyerId) {
        var carts = getCartsFromCacheOrDatabase(buyerId);

        var userCarts = carts.stream()
                .map(CART_MAPPER::toUserCartsResponse)
                .toList();

        return groupCartsBySupplier(userCarts);
    }

    private List<CartEntity> getCartsFromCacheOrDatabase(Long buyerId) {
        List<CartEntity> carts = getCartsFromCache(buyerId);

        if (carts.isEmpty()) {
            carts = cartRepository.findByBuyerIdAndStatusOrderByCreatedAtDesc(buyerId, CartStatus.ACTIVE);
            writeCartsToCache(buyerId, carts);
        }

        return carts;
    }

    private void writeCartsToCache(Long buyerId, List<CartEntity> carts) {
        if (carts != null && !carts.isEmpty()) {
            cartCacheService.saveUserCartsToCache(buyerId, carts);
        }
    }

    private List<CartEntity> getCartsFromCache(Long buyerId) {
        var carts = cartCacheService.getUserCartsFromCache(buyerId);
        return (carts != null) ? carts : Collections.emptyList();
    }

    private List<GroupedCartsBySupplier> groupCartsBySupplier(List<UserCarts> userCarts) {
        return userCarts.stream()
                .collect(Collectors.groupingBy(UserCarts::getSupplierId))
                .entrySet().stream()
                .map(entry -> GroupedCartsBySupplier.builder()
                        .supplierId(entry.getKey())
                        .carts(entry.getValue())
                        .build())
                .toList();
    }

    private CartEntity fetchCartIfExist(Long buyerId, Long productId) {
        return cartRepository.findByBuyerIdAndProductIdAndStatus(buyerId, productId, CartStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(CART_NOT_FOUND_CODE + buyerId, CART_NOT_FOUND_MESSAGE));
    }

    private CartEntity updateExistingCart(CartEntity existingCart, CartRequest cartRequest) {
        existingCart.setQuantity(cartRequest.getQuantity());
        return existingCart;
    }

    private CartEntity buildNewCart(CartRequest cartRequest) {
        return CART_MAPPER.toEntity(cartRequest);
    }
}