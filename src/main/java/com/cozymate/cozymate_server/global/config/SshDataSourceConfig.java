package com.cozymate.cozymate_server.global.config;


import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SshDataSourceConfig {

    private final SshTunnelConfig initializer;

    @Value("${server}")
    private String isServer;
    @Value("${cloud.aws.ec2.database_endpoint}")
    private String databaseEndpoint;

    @Bean("dataSource")
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        String url = properties.getUrl().replace("localhost", databaseEndpoint);
        url = url.replace("[forwardedPort]", String.valueOf(3306));
        Integer forwardedPort = null;
        if (isServer.equals("false")) {
            url = properties.getUrl(); // jdbc:mysql://localhost:[forwardedPort]/dev
            forwardedPort = initializer.buildSshConnection();
            url = url.replace("[forwardedPort]", String.valueOf(forwardedPort));
        }

        log.info("Datasource Properties URL: {}", url);
        log.info("Datasource Properties Username: {}", properties.getUsername());
        log.info("Datasource Properties Driver ClassName: {}", properties.getDriverClassName());

        log.info(url);
        return DataSourceBuilder.create()
            .url(url)
            .username(properties.getUsername())
            .password(properties.getPassword())
            .driverClassName(properties.getDriverClassName())
            .build();
    }
}
