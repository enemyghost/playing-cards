package com.gmo.big2.api.config;

import javax.annotation.Resource;

import java.util.List;

import com.gmo.big2.api.store.ObjectMapperSingleton;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmo.big.two.auth.utils.JwtUtils;
import com.gmo.big2.api.servlet.JwtAuthenticationHandlerInterceptor;

/**
 * @author tedelen
 */
@Configuration
@ComponentScan(basePackages = { "com.gmo.big2.api.config", "com.gmo.big2.api.controller" })
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Resource
    private JwtUtils jwtUtils;

    @Override
    protected void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter(ObjectMapperSingleton.OBJECT_MAPPER));
        addDefaultHttpMessageConverters(converters);
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new JwtAuthenticationHandlerInterceptor(jwtUtils))
                .addPathPatterns("/v1/**")
                .excludePathPatterns("/v1/auth/login")
                .excludePathPatterns("/v1/auth/register")
                .excludePathPatterns("/v1/leaderboard")
                .excludePathPatterns("/v1/fullHistory**");
    }
}
