package org.ingress.cartms.exception;

public interface ExceptionConstraints {
    String UNEXPECTED_EXCEPTION_CODE = "UNEXPECTED_EXCEPTION";
    String UNEXPECTED_EXCEPTION_MESSAGE = "UNEXPECTED_EXCEPTION";
    String CART_NOT_FOUND_CODE = "CART_NOT_FOUND";
    String CART_NOT_FOUND_MESSAGE = "Cart not found for id: ";

    String CLIENT_ERROR_MESSAGE = "Client Error";
    String CLIENT_ERROR_CODE = "Client Error";
}