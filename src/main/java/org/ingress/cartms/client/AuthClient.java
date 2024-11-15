package org.ingress.cartms.client;

import org.ingress.cartms.client.decoder.CustomErrorDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@FeignClient(name= "ms-auth",
        url = "${client.ms-auth.url}",
        path = "/v1/verify", configuration = CustomErrorDecoder.class)
public interface AuthClient {

    @GetMapping
    void verify(@RequestHeader(AUTHORIZATION) String accessToken);
}