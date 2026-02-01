package com.smartpricing.service.ai;

import com.smartpricing.dto.ai.NegotiationAIRequestDto;
import com.smartpricing.dto.ai.NegotiationAIResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AINegotiationClient {

    private final WebClient aiWebClient;

    public NegotiationAIResponseDto negotiate(NegotiationAIRequestDto request) {

        return aiWebClient.post()
                .uri("/ai/negotiate")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(NegotiationAIResponseDto.class)
                .timeout(Duration.ofSeconds(3))   // ⏱️ FAIL FAST
                .onErrorResume(ex ->
                        Mono.empty()              // fallback handled in service
                )
                .block();
    }
}
