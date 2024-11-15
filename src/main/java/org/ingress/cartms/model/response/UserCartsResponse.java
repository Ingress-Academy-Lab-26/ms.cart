package org.ingress.cartms.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ingress.cartms.model.enums.CartStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCartsResponse {
    private Long supplierId;
    private Long productId;
    private Integer quantity;
    private CartStatus status;
    private LocalDateTime createdAt;
}
