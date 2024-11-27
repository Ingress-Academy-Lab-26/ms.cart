package org.ingress.cartms.model.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static org.ingress.cartms.model.constants.ValidationMessages.*;

@Data
@Builder
public class CartRequest {

    @NotNull(message = BUYER_ID_REQUIRED)
    private Long buyerId;

    @NotNull(message = PRODUCT_ID_REQUIRED)
    private Long productId;

    @NotNull(message = QUANTITY_REQUIRED)
    @Min(value = 1, message = QUANTITY_MINIMUM)
    private Integer quantity;

}