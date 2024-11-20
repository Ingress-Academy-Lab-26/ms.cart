package org.ingress.cartms.service.concrete;

import lombok.RequiredArgsConstructor;
import org.ingress.cartms.client.AuthClient;
import org.ingress.cartms.model.auth.AuthDto;
import org.springframework.stereotype.Service;

@Service(value = "authService")
@RequiredArgsConstructor
public class AuthService {
    private final AuthClient authClient;

    public AuthDto verifyToken(String accessToken){
        return authClient.verifyToken(accessToken);

    }
}