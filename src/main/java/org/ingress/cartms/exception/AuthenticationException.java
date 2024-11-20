package org.ingress.cartms.exception;

import lombok.Getter;

@Getter
public class AuthenticationException extends RuntimeException {
    private final String code;
    private final int status;

    public AuthenticationException(String message, String code, int status) {
        super(message);
        this.code = code;
        this.status = status;
    }
}
