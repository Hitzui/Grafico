package com.dysconcsa.app.grafico;

import com.dysconcsa.app.grafico.util.Utility;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationApp {
    @Bean
    public Utility utility(){
        return new Utility();
    }
}
