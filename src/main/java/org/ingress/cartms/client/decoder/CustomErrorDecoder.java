package org.ingress.cartms.client.decoder;

import static org.ingress.cartms.client.decoder.JsonNodeFieldName.CODE;
import static org.ingress.cartms.client.decoder.JsonNodeFieldName.MESSAGE;
import static org.ingress.cartms.exception.ExceptionConstraints.*;

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
        var errorMessage = CLIENT_ERROR_MESSAGE;
        var errorCode =CLIENT_ERROR_CODE;
        var statusCode = response.status();

        JsonNode jsonNode;
        try (var body = response.body().asInputStream()) {
            jsonNode = new ObjectMapper().readValue(body, JsonNode.class);
        } catch (Exception e) {
            log.error("Failed to parse response body for method {} with status {}", methodKey, statusCode, e);
            return new CustomFeignException(CLIENT_ERROR_MESSAGE, CLIENT_ERROR_CODE, statusCode);
        }
        if (jsonNode.has(MESSAGE.getValue())) {
            errorMessage = jsonNode.get(MESSAGE.getValue()).asText();
        }
        if (jsonNode.has(CODE.getValue())) {
            errorCode = jsonNode.get(CODE.getValue()).asText();
        }

        return new CustomFeignException(errorMessage , errorCode, statusCode);



    }
}
