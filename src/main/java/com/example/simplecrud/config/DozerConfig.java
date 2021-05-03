package com.example.simplecrud.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.github.dozermapper.spring.DozerBeanMapperFactoryBean;

@Configuration
public class DozerConfig {
    @Bean
    public DozerBeanMapperFactoryBean dozerMapper(
            ResourcePatternResolver resourcePatternResolver) throws IOException {
        DozerBeanMapperFactoryBean factoryBean = new DozerBeanMapperFactoryBean();
        factoryBean.setMappingFiles(
                resourcePatternResolver.getResources("classpath*:/META-INF/dozer/**/*-mapping.xml"));
        // ...
        return factoryBean;
    }
}
