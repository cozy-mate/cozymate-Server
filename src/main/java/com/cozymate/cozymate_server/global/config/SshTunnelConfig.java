package com.cozymate.cozymate_server.global.config;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import jakarta.annotation.PreDestroy;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Component
@Validated
@Setter
public class SshTunnelConfig {

    @Value("${ssh_tunnel.host}")
    private String host;
    @Value("${ssh_tunnel.port}")
    private int sshPort;
    @Value("${ssh_tunnel.user}")
    private String user;
    @Value("${ssh_tunnel.private_key_path}")
    private String privateKeyPath;

    private Session session;

    @PreDestroy
    public void destroy() {
        if (session.isConnected()) {
            session.disconnect();
        }
    }

    public Integer buildSshConnection(String endpoint, int port) {
        Integer forwardPort = null;

        try {
            log.info("SSH {}@{}:{}", user, host, sshPort);

            JSch jsch = new JSch();
            session = jsch.getSession(user, host, sshPort);
            session.setConfig("StrictHostKeyChecking", "no");

            // pem 기반 인증 먼저 확인
            log.info("Using private key auth: {}", privateKeyPath);
            jsch.addIdentity(privateKeyPath);

            log.info("Starting SSH session connection...");
            session.connect();
            log.info("SSH session connected");

            forwardPort = session.setPortForwardingL(0, endpoint, port);
            log.info("Port forwarding created on local port {} to remote port {}", forwardPort, port);

        } catch (JSchException e) {
            log.error("SSH 연결 실패", e);
            this.destroy();
            throw new RuntimeException(e);
        }

        return forwardPort;
    }

}
