package org.ingress.cartms.service.concrete;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ingress.cartms.annotation.Log;
import org.ingress.cartms.client.ProductClient;
import org.ingress.cartms.dao.entity.CartEntity;
import org.ingress.cartms.dao.repository.CartRepository;
import org.ingress.cartms.exception.NotFoundException;
import org.ingress.cartms.mapper.CartMapper;
import org.ingress.cartms.model.enums.CartStatus;
import org.ingress.cartms.model.request.CartRequest;
import org.ingress.cartms.model.response.UserCartsResponse;
import org.ingress.cartms.service.abstracts.CartCacheService;
import org.ingress.cartms.service.abstracts.CartService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import static org.ingress.cartms.exception.ExceptionConstraints.CART_NOT_FOUND_CODE;
import static org.ingress.cartms.exception.ExceptionConstraints.CART_NOT_FOUND_MESSAGE;




@Log
@Slf4j
@Service
@RequiredArgsConstructor

public class CartServiceHandler implements CartService {

    private final CartRepository cartRepository;
    private final ProductClient productClient;
    private final CartCacheService cartCacheService;


    @Override
    public void insertOrUpdateCart(CartRequest cartRequest) {

        CartEntity cartEntity = fetchOrCreateCart(cartRequest);

        cartEntity.setSupplierId(productClient.getSupplierByProductId(cartEntity.getProductId()));
        cartRepository.save(cartEntity);

        cartCacheService.saveUserCartToCache(cartRequest.getBuyerId(), cartEntity);

    }

    private CartEntity fetchOrCreateCart(CartRequest cartRequest) {

        return getCartFromCacheOrDatabase(cartRequest)
                .map(existingCart -> updateExistingCart(existingCart, cartRequest))
                .orElseGet(() -> createNewCart(cartRequest));
    }

    private Optional<CartEntity> getCartFromCacheOrDatabase(CartRequest cartRequest) {
        Optional<CartEntity> cachedCart = cartCacheService.getUserCartFromCache(cartRequest.getBuyerId(), cartRequest.getProductId());

        if (cachedCart.isEmpty()) {
            cachedCart = cartRepository.findByBuyerIdAndProductId(cartRequest.getBuyerId(), cartRequest.getProductId());
        }
        return cachedCart;
    }

    @Override
    public void deleteCart(Long buyerId, Long productId) {

            CartEntity cart = fetchCartIfExist(buyerId, productId);
            cart.setStatus(CartStatus.DELETED);
            cartRepository.save(cart);

            cartCacheService.deleteUserCartFromCache(buyerId, productId);

    }
    @Override
    public Map<Long, List<UserCartsResponse>> getCartsByUserId(Long buyerId) {
        List<CartEntity> carts = getCartsFromCacheOrDatabase(buyerId);

        List<UserCartsResponse> userCarts = processCarts(carts);

        return groupCartsBySupplier(userCarts);
    }

    private List<CartEntity> getCartsFromCacheOrDatabase(Long buyerId) {
        List<CartEntity> carts = cartCacheService.getUserCartsFromCache(buyerId);

        if (carts == null || carts.isEmpty()) {
            carts = cartRepository.findByBuyerId(buyerId);
            cartCacheService.saveUserCartsToCache(buyerId, carts);
        }

        return carts;
    }

    private List<UserCartsResponse> processCarts(List<CartEntity> carts) {
        return carts.stream()
                .filter(this::isActiveCart)
                .sorted(this::compareByCreatedAtDescending)
                .map(CartMapper::toUserCartsResponse)
                .toList();
    }

    private boolean isActiveCart(CartEntity cart) {
        return cart.getStatus() == CartStatus.ACTIVE;
    }

    private int compareByCreatedAtDescending(CartEntity c1, CartEntity c2) {
        return c2.getCreatedAt().compareTo(c1.getCreatedAt());
    }

    private Map<Long, List<UserCartsResponse>> groupCartsBySupplier(List<UserCartsResponse> userCarts) {
        return userCarts.stream()
                .collect(Collectors.groupingBy(UserCartsResponse::getSupplierId));
    }

    private CartEntity fetchCartIfExist(Long buyerId, Long productId) {
        return cartRepository.findByBuyerIdAndProductId(buyerId, productId)
                .orElseThrow(() -> new NotFoundException(CART_NOT_FOUND_CODE + buyerId, CART_NOT_FOUND_MESSAGE));
    }

    private CartEntity updateExistingCart(CartEntity existingCart, CartRequest cartRequest) {
        existingCart.setQuantity(cartRequest.getQuantity());
        return existingCart;
    }

    private CartEntity createNewCart(CartRequest cartRequest) {
        return CartMapper.toEntity(cartRequest);
    }
}