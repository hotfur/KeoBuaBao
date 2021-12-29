package com.KeoBuaBao;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Cross-Origin Resource Sharing Policy
 * Allow resource sharing between api.keobuabao.com and keobuabao.com for all packet.
 */
@Configuration
public class CORS implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
         .allowedMethods("*")
         .allowedHeaders("*")
         .allowedOrigins("https://keobuabao.com")
         .allowCredentials(false)
         .maxAge(-1);
         }
}
