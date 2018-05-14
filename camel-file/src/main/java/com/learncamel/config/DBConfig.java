package com.learncamel.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DBConfig {

    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "spring.dataSource")
    public DataSource dataSource(){
        return DataSourceBuilder.create().build();
    }

}
