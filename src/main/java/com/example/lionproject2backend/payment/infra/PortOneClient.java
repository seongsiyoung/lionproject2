package com.example.lionproject2backend.payment.infra;

import com.example.lionproject2backend.payment.config.PortOneProperties;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class PortOneClient {
    private static final String BASE_URL = "https://api.portone.io";

    private final PortOneProperties portOneProperties;
    private final RestClient restClient = RestClient.create(BASE_URL);

    public Map<String, Object> getPaymentDetails(String paymentId) {
        Map<String, Object> response = restClient.get()
            .uri("/payments/{paymentId}", paymentId)
            .header("Authorization", "PortOne " + portOneProperties.apiSecret())
            .retrieve()
            .body(Map.class);

        if (response == null) {
            throw new IllegalStateException("PortOne response is empty");
        }
        return response;
    }
}
