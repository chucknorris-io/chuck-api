package io.chucknorris.api.configuration;

public interface DataSourceProperties {

    String getDriverClassName();

    String getPassword();

    String getUrl();

    String getUsername();
}
