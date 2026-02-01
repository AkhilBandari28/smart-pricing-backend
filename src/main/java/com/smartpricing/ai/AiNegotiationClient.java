//package com.smartpricing.ai;
//
//import com.smartpricing.ai.dto.AiNegotiationRequest;
//import com.smartpricing.ai.dto.AiNegotiationResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//@Component
//public class AiNegotiationClient {
//
//    private static final String AI_URL =
//            "http://localhost:8000/ai/negotiate";
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    public AiNegotiationResponse negotiate(AiNegotiationRequest request) {
//
//        return restTemplate.postForObject(
//                AI_URL,
//                request,
//                AiNegotiationResponse.class
//        );
//    }
//}





package com.smartpricing.ai;

import com.smartpricing.ai.dto.AiNegotiationRequest;
import com.smartpricing.ai.dto.AiNegotiationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AiNegotiationClient {

    private final WebClient aiWebClient;

    public AiNegotiationResponse negotiate(AiNegotiationRequest request) {

        return aiWebClient.post()
                .uri("/ai/negotiate")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AiNegotiationResponse.class)
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(ex -> Mono.empty())
                .block();
    }
}

