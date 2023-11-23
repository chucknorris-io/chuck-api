package io.chucknorris.api.configuration;

import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(prefix = "spring.datasource", name = "url")
@Configuration
@Data
@Getter
public class DefaultDataSourceProperties implements DataSourceProperties {

    private String driverClassName;
    private String password;
    private String url;
    private String username;

    public DefaultDataSourceProperties(
            @Value("${spring.datasource.driver-class-name}") String driverClassName,
            @Value("${spring.datasource.password}") String password,
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username
    ) {
        this.driverClassName = driverClassName;
        this.password = password;
        this.url = url;
        this.username = username;
    }
}
