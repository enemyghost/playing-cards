package com.gmo.big2.api.config;

import javax.sql.DataSource;

import java.util.Optional;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class MySqlStoreConfig {
    @Bean
    public DataSource dataSource() {
        final String hostname = databaseHostname();
        String jdbcUrl = String.format("jdbc:mysql://%s:%d/big_two", hostname, databasePort());
        if (hostname.equals("localhost")) {
            jdbcUrl = jdbcUrl + "?serverTimezone=UTC";
        }

        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(databaseUsername());
        hikariConfig.setPassword(databasePassword());
        return new HikariDataSource(hikariConfig);
    }

    @Bean
    public DSLContext dslContext(final DataSource dataSource) {
        return new DefaultDSLContext(dataSource, SQLDialect.MYSQL_8_0);
    }

    private String databaseHostname() {
        return Optional.ofNullable(System.getenv("BIG2_MYSQL_HOSTNAME")).orElse("localhost");
    }

    private int databasePort() {
        return Integer.valueOf(Optional.ofNullable(System.getenv("BIG2_MYSQL_PORT")).orElse("3306"));
    }
    private String databaseUsername() {
        return Optional.ofNullable(System.getenv("BIG2_MYSQL_USERNAME")).orElse("root");
    }

    private String databasePassword() {
        return Optional.ofNullable(System.getenv("BIG2_MYSQL_PASSWORD")).orElse("");
    }
}
