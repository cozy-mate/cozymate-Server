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
import software.amazon.awssdk.services.s3.endpoints.internal.Value.Bool;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableJpaRepositories(basePackages = "com.cozymate.cozymate_server.domain")
public class SshDataSourceConfig {

    private final SshTunnelConfig initializer;

    @Value("${server}")
    private boolean noSshTunneling;

    @Value("${ssh_tunnel.database_tunnel_endpoint}")
    private String databaseTunnelEndppoint;
    @Value("${ssh_tunnel.database_endpoint}")
    private String databaseEndpoint;
    @Value("${ssh_tunnel.database_port}")
    private int databasePort;


    @Bean("dataSource")
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        String url = properties.getUrl(); // jdbc:p6spy:mysql://[host]:[port]/...

        // SSH 터널을 통해 RDS에 연결해야 할 경우
        if (!noSshTunneling) {
            // 터널링 사용하는 경우: 로컬 포트로 바꿔 끼우기
            int forwardedPort = initializer.buildSshConnection(databaseTunnelEndppoint, databasePort);
            url = url
                .replace("[host]", databaseTunnelEndppoint)
                .replace("[port]", String.valueOf(forwardedPort));
        } else {
            // 터널 없이 infra-mysql 로 직접 붙는 경우
            url = url
                .replace("[host]", databaseEndpoint)
                .replace("[port]", String.valueOf(databasePort));
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
