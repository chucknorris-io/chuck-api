package io.chucknorris.api.configuration;

import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@ConditionalOnProperty(prefix = "spring.datasource", name = "uri")
@Configuration
@Data
@Getter
public class HerokuDataSourceProperties implements DataSourceProperties {

    private static final String SCHEMA_POSTGRESQL = "postgresql";

    private String driverClassName;
    private String password;
    private String url;
    private String username;

    public HerokuDataSourceProperties(
            @Value("${spring.datasource.driver-class-name}") String driverClassName,
            @Value("${spring.datasource.uri}") String uniformResourceIdentifier
    ) {
        URI uri = URI.create(uniformResourceIdentifier);

        String[] userInfo = uri.getUserInfo().split(":");

        this.driverClassName = driverClassName;
        this.password = userInfo[1];
        this.url = String.format(
                "jdbc:%s://%s:%s/%s",
                this.scheme(uri),
                uri.getHost().replace("/", ""),
                uri.getPort(),
                uri.getPath().replace("/", "")
        );
        this.username = userInfo[0];
    }

    private String scheme(URI uri) {
        if ("postgres".compareToIgnoreCase(uri.getScheme()) == 0) {
            return SCHEMA_POSTGRESQL;
        }

        throw new RuntimeException(
                String.format("Unsupported schema %s given", uri.getScheme())
        );
    }
}
