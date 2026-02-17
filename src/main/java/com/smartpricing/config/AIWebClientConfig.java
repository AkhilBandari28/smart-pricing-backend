//package com.smartpricing.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.reactive.function.client.WebClient;
//
//@Configuration
//public class AIWebClientConfig {
//
//    @Bean
//    public WebClient aiWebClient() {
//        return WebClient.builder()
//                .baseUrl("http://localhost:8000") // Python AI service
//                .build();
//    }
//}


package com.smartpricing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AIWebClientConfig {

//    @Value("${ai.service.url}")
//    private String aiServiceUrl;

    @Bean
    public WebClient aiWebClient() {
        return WebClient.builder()
                .baseUrl("https://smart-pricing-ai-cx0n.onrender.com")
                .build();
    }
}
