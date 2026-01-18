package com.zote.common.utils.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String OAUTH_SCHEME_NAME = "oauth2";

    private static final String TOKEN_SCHEME_NAME = "bearer";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Lz-insurance API")
                        .description("API documentation for LZ-insurance")
                        .version("1.0.0"))
//                .addSecurityItem(new SecurityRequirement().addList(OAUTH_SCHEME_NAME))
                .addSecurityItem(new SecurityRequirement().addList(TOKEN_SCHEME_NAME))
                .components(new Components()
//                        .addSecuritySchemes(OAUTH_SCHEME_NAME, new SecurityScheme()
//                                .name(OAUTH_SCHEME_NAME)
//                                .type(SecurityScheme.Type.OAUTH2)
//                                .flows(new OAuthFlows()
//                                        .authorizationCode(new OAuthFlow()
//                                                .authorizationUrl("http://localhost:8080/realms/real-estate/protocol/openid-connect/auth")
//                                                .tokenUrl("http://localhost:8080/realms/real-estate/protocol/openid-connect/token"))))
                        .addSecuritySchemes(TOKEN_SCHEME_NAME, new SecurityScheme()
                                .name(TOKEN_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme(TOKEN_SCHEME_NAME)
                                .bearerFormat("JWT"))
                );
    }

}
