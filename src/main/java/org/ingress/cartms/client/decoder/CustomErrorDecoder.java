package org.ingress.cartms.client.decoder;

import static org.ingress.cartms.client.decoder.JsonNodeFieldName.MESSAGE;
import static org.ingress.cartms.exception.ExceptionConstraints.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.ingress.cartms.exception.CustomFeignException;


@Slf4j
public class CustomErrorDecoder implements ErrorDecoder {



    @Override
    public Exception decode(String methodKey, Response response) {
        var errorMessage = CLIENT_ERROR;
        var statusCode = response.status();

        JsonNode jsonNode;
        try (var body = response.body().asInputStream()) {
            jsonNode = new ObjectMapper().readValue(body, JsonNode.class);
        } catch (Exception e) {
            log.error("Failed to parse response body for method {} with status {}", methodKey, statusCode, e);
            return new CustomFeignException(CLIENT_ERROR, statusCode);
        }

        if (statusCode >= 400 && statusCode < 500) {
            if (jsonNode.has(MESSAGE.getValue())) {
                errorMessage = jsonNode.get(MESSAGE.getValue()).asText();
            }
            log.error("Client error while calling method {} with status {} and message: {}",
                    methodKey, statusCode, errorMessage);
            return new CustomFeignException(errorMessage, statusCode);
        }

        if (statusCode >= 500) {
            errorMessage = jsonNode.has(MESSAGE.getValue()) ? jsonNode.get(MESSAGE.getValue()).asText() : String.valueOf(SERVER_ERROR);
            log.error("Server error while calling method {} with status {} and message: {}",
                    methodKey, statusCode, errorMessage);
            return new CustomFeignException(errorMessage, statusCode);
        }

        log.warn("Unexpected error while calling method {} with status {} and message: {}",
                methodKey, statusCode, errorMessage);
        return new CustomFeignException("Unexpected error: " + errorMessage, statusCode);

    }
}
