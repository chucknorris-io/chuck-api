package io.chucknorris.api.configuration;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HerokuDataSourcePropertiesTest {

    @Test
    public void testShouldCreateDataSourcePostgres() {
        // given:
        String driverClassName = "org.postgresql.Driver";

        // and:
        String uri = "postgres://username:password@ec2-00-000-00-000.eu-west-1.compute.amazonaws.com:5432/d409qm4ujtafvk";

        // when:
        HerokuDataSourceProperties actual = new HerokuDataSourceProperties(driverClassName, uri);

        // then:
        assertEquals("org.postgresql.Driver", actual.getDriverClassName());
        assertEquals("password", actual.getPassword());
        assertEquals("jdbc:postgresql://ec2-00-000-00-000.eu-west-1.compute.amazonaws.com:5432/d409qm4ujtafvk", actual.getUrl());
        assertEquals("username", actual.getUsername());
    }
}