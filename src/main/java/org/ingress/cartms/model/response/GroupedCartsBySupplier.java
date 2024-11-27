package org.ingress.cartms.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupedCartsBySupplier {
    private Long supplierId;
    private List<UserCarts> carts;
}
