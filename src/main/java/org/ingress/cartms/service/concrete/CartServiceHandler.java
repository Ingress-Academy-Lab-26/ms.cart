package org.ingress.cartms.service.concrete;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ingress.cartms.annotation.Log;
import org.ingress.cartms.client.ProductClient;
import org.ingress.cartms.dao.entity.CartEntity;
import org.ingress.cartms.exception.DataAccessException;
import org.ingress.cartms.dao.repository.CartRepository;
import org.ingress.cartms.exception.NotFoundException;
import org.ingress.cartms.mapper.CartMapper;
import org.ingress.cartms.model.enums.CartStatus;
import org.ingress.cartms.model.request.CartRequest;
import org.ingress.cartms.model.response.UserCartsResponse;
import org.ingress.cartms.service.abstracts.CartCacheService;
import org.ingress.cartms.service.abstracts.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
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
    @Transactional
    public void insertOrUpdateCart(CartRequest cartRequest) {

            CartEntity cartEntity = cartRepository.findByBuyerIdAndProductId(cartRequest.getBuyerId(), cartRequest.getProductId())
                    .map(existingCart -> updateExistingCart(existingCart, cartRequest))
                    .orElseGet(() -> createNewCart(cartRequest));

            cartEntity.setSupplierId(productClient.getSupplierByProductId(cartEntity.getProductId()));
            cartRepository.save(cartEntity);

            cartCacheService.saveUserCartToCache(cartRequest.getBuyerId(), cartEntity);

    }

    @Override
    @Transactional
    public void deleteCart(Long buyerId, Long productId) {

            CartEntity cart = fetchCartIfExist(buyerId, productId);
            cart.setStatus(CartStatus.DELETED);
            cartRepository.save(cart);

            cartCacheService.deleteUserCartFromCache(buyerId, productId);

    }


    @Override
    public Map<Long, List<UserCartsResponse>> getCartsByUserId(Long buyerId) {

            List<CartEntity> carts = cartRepository.findByBuyerId(buyerId);

            carts.forEach(cart -> cartCacheService.saveUserCartToCache(buyerId, cart));
//list formasinda cache ye yazmaq
            List<UserCartsResponse> userCarts = carts.stream()
                    .filter(cart -> cart.getBuyerId().equals(buyerId) && cart.getStatus() == CartStatus.ACTIVE)
                    .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                    .map(CartMapper::toUserCartsResponse)
                    .toList();

            Map<Long, List<UserCartsResponse>> groupedCarts = userCarts.stream()
                    .collect(Collectors.groupingBy(UserCartsResponse::getSupplierId));


            return groupedCarts;

    }

    /**
     * Fetches a cart entity if it exists; throws NotFoundException otherwise.
     *
     * @param buyerId   the unique identifier of the user
     * @param productId the unique identifier of the product
     * @return the fetched CartEntity
     */
    private CartEntity fetchCartIfExist(Long buyerId, Long productId) {
        return cartRepository.findByBuyerIdAndProductId(buyerId, productId)
                .orElseThrow(() -> new NotFoundException(CART_NOT_FOUND_CODE + buyerId, CART_NOT_FOUND_MESSAGE));
    }

    /**
     * Updates an existing cart entity with new details.
     *
     * @param existingCart the cart entity to be updated
     * @param cartRequest  the request containing updated cart details
     * @return the updated CartEntity
     */
    private CartEntity updateExistingCart(CartEntity existingCart, CartRequest cartRequest) {
        existingCart.setQuantity(cartRequest.getQuantity());
        log.debug("Updated cart quantity for cartId: {}", existingCart.getId());
        return existingCart;
    }

    /**
     * Creates a new cart entity from the request.
     *
     * @param cartRequest the request containing cart details
     * @return the newly created CartEntity
     */
    private CartEntity createNewCart(CartRequest cartRequest) {
        CartEntity newCart = CartMapper.toEntity(cartRequest);
        log.debug("Created new cart for buyerId: {}, productId: {}", cartRequest.getBuyerId(), cartRequest.getProductId());
        return newCart;
    }
}


