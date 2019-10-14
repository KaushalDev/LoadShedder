package com.kaushaldev.loadshedder.core.springbootconfiguration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LoadShedderSpringBootConfiguration {
    @Bean
    @ConditionalOnWebApplication
    public WebMvcConfigurer interceptorAdapter() {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(loadShedderRequestIntercepter());
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnWebApplication
    public LoadShedderRequestIntercepter loadShedderRequestIntercepter() {
        return new LoadShedderRequestIntercepter();
    }


}
