package org.ingress.cartms.model.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class AuthDto {
    private Long userId;
}
