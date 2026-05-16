package com.conti_talent.springboot.appweb.conti_talent_web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuracion MVC: Spring Boot ya sirve por defecto static/** y templates/**,
 * pero declaramos explicitamente los handlers para dejar el contrato visible.
 * No se mueve ningun archivo existente del frontend.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("/img/**", "/images/**", "/assets/**")
                .addResourceLocations("classpath:/static/img/", "classpath:/static/images/", "classpath:/static/assets/");
    }
}
