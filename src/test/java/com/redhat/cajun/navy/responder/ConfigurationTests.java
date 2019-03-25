package com.redhat.cajun.navy.responder;

import com.redhat.cajun.navy.responder.service.ResponderService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class ConfigurationTests {

    @Primary
    @Bean
    public ResponderService responderService() {
        return Mockito.mock(ResponderService.class);
    }

}
