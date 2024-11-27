package org.ingress.cartms.exception;

import lombok.Getter;

@Getter
public class CustomFeignException extends RuntimeException {
    private String code;
    private final int status;

    public CustomFeignException(String message, String code, int status) {
        super(message);
        this.code = code;
        this.status = status;
    }
}
