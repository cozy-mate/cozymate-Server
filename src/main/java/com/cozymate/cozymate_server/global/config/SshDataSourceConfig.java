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
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableJpaRepositories(basePackages = "com.cozymate.cozymate_server.domain")
public class SshDataSourceConfig {

    private final SshTunnelConfig initializer;

    @Value("${server}")
    private String isServer;

    @Value("${spring.cloud.aws.ec2.database_endpoint}")
    private String databaseEndpoint;
    @Value("${spring.cloud.aws.ec2.database_port}")
    private int databasePort;


    @Bean("dataSource")
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        String url = properties.getUrl().replace("localhost", databaseEndpoint);
        url = url.replace("[forwardedPort]", String.valueOf(3306));
        Integer forwardedPort = null;

        // SSH 터널을 통해 RDS에 연결해야 할 경우
        if (isServer.equals("false")) {
            url = properties.getUrl(); // jdbc:mysql://localhost:[forwardedPort]/dev
            forwardedPort = initializer.buildSshConnection(databaseEndpoint, databasePort);
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
