package org.ingress.cartms.exception;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MethodArgumentNotValidExceptionResponse {
    private String error;
    private List<ConstraintsViolationError> fieldErrors;
}
