package org.ingress.cartms.service.concrete;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ingress.cartms.dao.entity.CartEntity;
import org.ingress.cartms.dao.repository.CartRepository;
import org.ingress.cartms.exception.DataAccessException;
import org.ingress.cartms.service.abstracts.CartCacheService;
import org.ingress.cartms.util.CacheUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import io.github.resilience4j.retry.annotation.Retry;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.List;
import java.util.Optional;

import static org.ingress.cartms.util.CartCacheConstraints.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartCacheServiceHandler implements CartCacheService {

    private final CacheUtil cacheUtil;
    private final CartRepository cartRepository;


    @Async
    @CircuitBreaker(name = REDIS_CACHE_BREAKER, fallbackMethod = "fallbackSaveUserCartToCache")
    @Retry(name = REDIS_CACHE_RETRY, fallbackMethod = "fallbackSaveUserCartToCache")
    @Override
    public void saveUserCartToCache(Long buyerId, CartEntity cart) {


            String cacheKey = getUserCartCacheKey(buyerId, cart.getProductId());
            cacheUtil.saveToCache(cacheKey, cart, CART_CACHE_EXPIRATION_COUNT, CART_CACHE_EXPIRATION_UNIT);
            log.info("Cart for buyerId {} and productId {} saved to cache.", buyerId, cart.getProductId());
    }

    public void fallbackSaveUserCartToCache(Long buyerId, CartEntity cart, Throwable throwable) {
        log.error("Failed to save cart for buyerId {} and productId {} to cache due to Redis outage.", buyerId, cart.getProductId(), throwable);

    }


//    @CircuitBreaker(name = REDIS_CACHE_BREAKER, fallbackMethod = "fallbackGetUserCartFromCache")
//    @Override
//    public CartEntity getUserCartFromCache(Long buyerId, Long productId) {
//        return cacheUtil.getBucket(getUserCartCacheKey(buyerId, productId));
//    }

    @CircuitBreaker(name = REDIS_CACHE_BREAKER, fallbackMethod = "fallbackGetUserCartFromCache")
    @Override
    public Optional<CartEntity> getUserCartFromCache(Long buyerId, Long productId) {
        CartEntity cart = cacheUtil.getBucket(getUserCartCacheKey(buyerId, productId));
        return Optional.ofNullable(cart);
    }


    public CartEntity fallbackGetUserCartFromCache(Long buyerId, Long productId, Throwable throwable) {
        log.error("Failed to get cart for buyerId {} and productId {} from cache due to Redis outage.", buyerId, productId, throwable);
        return null;
    }


    @Async
    @CircuitBreaker(name = REDIS_CACHE_BREAKER, fallbackMethod = "fallbackUpdateUserCartInCache")
    @Retry(name = REDIS_CACHE_RETRY, fallbackMethod = "fallbackUpdateUserCartInCache")
    @Override
    public void updateUserCartInCache(Long buyerId, CartEntity cart) {
        cacheUtil.saveToCache(getUserCartCacheKey(buyerId, cart.getProductId()), cart, CART_CACHE_EXPIRATION_COUNT, CART_CACHE_EXPIRATION_UNIT);
        log.info("Cart for buyerId {} and productId {} updated in cache.", buyerId, cart.getProductId());
    }


    public void fallbackUpdateUserCartInCache(Long buyerId, CartEntity cart, Throwable throwable) {
        log.error("Failed to update cart for buyerId {} and productId {} in cache due to Redis outage.", buyerId, cart.getProductId(), throwable);
    }


    @Async
    @CircuitBreaker(name = REDIS_CACHE_BREAKER, fallbackMethod = "fallbackDeleteUserCartFromCache")
    @Retry(name = REDIS_CACHE_RETRY, fallbackMethod = "fallbackDeleteUserCartFromCache")
    @Override
    public void deleteUserCartFromCache(Long buyerId, Long productId) {
        cacheUtil.deleteFromCache(getUserCartCacheKey(buyerId, productId));
        log.info("Cart for buyerId {} and productId {} deleted from cache.", buyerId, productId);
    }


    public void fallbackDeleteUserCartFromCache(Long buyerId, Long productId, Throwable throwable) {
        log.error("Failed to delete cart for buyerId {} and productId {} from cache due to Redis outage.", buyerId, productId, throwable);
    }

    private String getUserCartCacheKey(Long buyerId, Long productId) {
        return CART_CACHE_KEY + buyerId + ":" + productId;
    }

    @Async
    @CircuitBreaker(name = "redisCacheBreaker", fallbackMethod = "fallbackSaveCartsToCache")
    @Retry(name = "redisCacheRetry", fallbackMethod = "fallbackSaveCartsToCache")
    @Override
    public void saveCartsToCache() {
        var cartEntityList = cartRepository.findAll();
        cacheUtil.saveToCache(CART_CACHE_KEY, cartEntityList, CART_CACHE_EXPIRATION_COUNT, CART_CACHE_EXPIRATION_UNIT);
        log.info("Carts saved to cache with key: {}", CART_CACHE_KEY);
    }

    @Async
    @CircuitBreaker(name = REDIS_CACHE_BREAKER, fallbackMethod = "fallbackSaveUserCartsToCache")
    @Retry(name = REDIS_CACHE_RETRY, fallbackMethod = "fallbackSaveUserCartsToCache")
    @Override
    public void saveUserCartsToCache(Long buyerId, List<CartEntity> carts) {
        try {
            String cacheKey = getUserCartListCacheKey(buyerId);

            cacheUtil.saveToCache(cacheKey, carts, CART_CACHE_EXPIRATION_COUNT, CART_CACHE_EXPIRATION_UNIT);

            log.info("Cart list for buyerId {} saved to cache.", buyerId);
        } catch (Exception e) {
            log.error("Failed to save cart list to cache for buyerId {}", buyerId, e);
        }
    }

    @CircuitBreaker(name = REDIS_CACHE_BREAKER, fallbackMethod = "fallbackGetUserCartsFromCache")
    @Override
    public List<CartEntity> getUserCartsFromCache(Long buyerId) {
        String cacheKey = getUserCartListCacheKey(buyerId);
        return cacheUtil.getBucket(cacheKey);
    }

    public List<CartEntity> fallbackGetUserCartsFromCache(Long buyerId, Throwable throwable) {
        log.error("Failed to get carts from cache for buyerId {}. Reason: {}", buyerId, throwable.getMessage());
        return List.of();
    }

    private String getUserCartListCacheKey(Long buyerId) {
        return CART_CACHE_KEY + buyerId;
    }


}






