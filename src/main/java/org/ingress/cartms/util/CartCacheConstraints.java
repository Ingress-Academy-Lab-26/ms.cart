package org.ingress.cartms.util;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public interface CartCacheConstraints {
    String CART_CACHE_KEY = "ms-cart:carts:";

    String SUPPLIER_CACHE_KEY = "ms-cart:supplier:";
    Long CART_CACHE_EXPIRATION_COUNT = 1L;
    TemporalUnit CART_CACHE_EXPIRATION_UNIT = ChronoUnit.DAYS;

    String REDIS_CACHE_BREAKER = "redisCacheBreaker";
    String REDIS_CACHE_RETRY = "redisCacheRetry";
}
