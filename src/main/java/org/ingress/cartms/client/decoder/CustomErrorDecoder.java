package org.ingress.cartms.client.decoder;

import static org.ingress.cartms.client.decoder.JsonNodeFieldName.MESSAGE;
import static org.ingress.cartms.exception.ExceptionConstraints.CLIENT_ERROR;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.ingress.cartms.exception.CustomFeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CustomErrorDecoder implements ErrorDecoder {

    private static final Logger log = LoggerFactory.getLogger(CustomErrorDecoder.class);

    @Override
    public Exception decode(String methodKey, Response response) {
        var errorMessage = CLIENT_ERROR;

        JsonNode jsonNode;
        try (var body = response.body().asInputStream()) {
            jsonNode = new ObjectMapper().readValue(body, JsonNode.class);
        } catch (Exception e) {
            throw new CustomFeignException(CLIENT_ERROR, response.status());
        }

        if (jsonNode.has(MESSAGE.getValue())) {
            errorMessage = jsonNode.get(MESSAGE.getValue()).asText();
        }

        log.error("ActionLog.decode.error Message: {}, Method: {}", errorMessage, methodKey);
        return new CustomFeignException(errorMessage, response.status());
    }
}
