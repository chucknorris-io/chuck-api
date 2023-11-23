package io.chucknorris.api.configuration;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(DataSourceProperties.class)
@EnableJpaRepositories(basePackages = "io.chucknorris")
@EnableTransactionManagement
@Slf4j
public class DataSourceConfiguration {

    @Bean
    public DataSource dataSource(DataSourceProperties dataSourceProperties) {
        log.info("Loading data source properties from {}", dataSourceProperties.getClass().getSimpleName());

        DataSource datasource = DataSourceBuilder.create()
                .driverClassName(dataSourceProperties.getDriverClassName())
                .password(dataSourceProperties.getPassword())
                .type(HikariDataSource.class)
                .url(dataSourceProperties.getUrl())
                .username(dataSourceProperties.getUsername())
                .build();

        log.info(String.format("DataSource({url=%s})", dataSourceProperties.getUrl()));

        return datasource;
    }
}
