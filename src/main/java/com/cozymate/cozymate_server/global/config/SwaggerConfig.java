package com.cozymate.cozymate_server.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.servers.Server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        // Define the security scheme
        SecurityScheme apiKey = new SecurityScheme()
                .type(Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        // Define the security requirement
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Authorization");

        // Add the security scheme to components
        Components components = new Components()
                .addSecuritySchemes("Authorization", apiKey);

        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .components(components)
                .addSecurityItem(securityRequirement)
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
