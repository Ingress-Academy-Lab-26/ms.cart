package org.ingress.cartms.client;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;


import java.util.concurrent.ThreadLocalRandom;


@Component
@Slf4j
public class ProductClient {

    public Long getSupplierByProductId(Long productId){
        Long supplierId = ThreadLocalRandom.current().nextLong(1, 11);
        log.info("Generated supplierId {} for productId {}", supplierId, productId);
        return supplierId;
    }
}
