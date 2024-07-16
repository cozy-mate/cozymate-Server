package com.cozymate.cozymate_server.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .addServersItem(new Server().url("/"))
            .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
            .title("Cozymate Spring Swagger")
            .description("Cozymate Swagger Doc입니다.")
            .version("0.0.1");
            //.contact(new Contact().name("gmail").url("https://www.google.com/intl/ko/gmail/about/"))
            //.description("잘못된 부분이나 오류 발생 시 해당 메일로 문의해주세요."); // TODO: 추가 예정
    }

}