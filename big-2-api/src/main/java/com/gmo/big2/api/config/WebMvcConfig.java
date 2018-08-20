package com.gmo.big2.api.config;

import java.util.List;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author tedelen
 */
@Configuration
@ComponentScan(basePackages = { "com.gmo.big2.api.config", "com.gmo.big2.api.controller" })
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Override
    protected void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter(new ObjectMapper()));

        addDefaultHttpMessageConverters(converters);
    }
}
