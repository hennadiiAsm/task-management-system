package ru.effectivemobile.tms.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@SecuritySchemes({
        @SecurityScheme(name = "bearerToken", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT"),
})
public class OpenApiConfig {

    @Bean
    public OpenAPI getOpenApiDocumentation() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task Management System")
                        .description("Task Management System API description")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Hennadii Asmolov")
                                .url("https://www.linkedin.com/in/hennadii-asmolov-a43478265/")
                                .email("asmolovgy@gmail.com"))
                        .termsOfService("MY TERMS OF SERVICE")
                        .license(new License()
                                .name("MY LICENSE")
                                .url("MY LICENSE URL"))
                )
                .security(List.of(new SecurityRequirement().addList("bearerToken")))
                .externalDocs(new ExternalDocumentation()
                        .description("MY WIKI PAGE")
                        .url("MY WIKI URL")
                );
    }

}
