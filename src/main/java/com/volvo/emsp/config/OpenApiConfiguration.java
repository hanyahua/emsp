package com.volvo.emsp.config;

import com.volvo.emsp.rest.model.ErrorResponse;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.volvo.emsp")
public class OpenApiConfiguration {

    @Bean
    public OpenAPI openAPI() {
        ApiResponse badRequestResponse = new ApiResponse()
                .description("Bad Request - Invalid input parameters")
                .content(createErrorResponseContent());

        ApiResponse notFoundResponse = new ApiResponse()
                .description("Resource not found")
                .content(createErrorResponseContent());

        ApiResponse conflictResponse = new ApiResponse()
                .description("Resource already exists")
                .content(createErrorResponseContent());

        return new OpenAPI()
                .components(
                    new Components()
                        .addResponses("BadRequest", badRequestResponse)
                        .addResponses("NotFound", notFoundResponse)
                        .addResponses("Conflict", conflictResponse)

                        .addSchemas("ErrorResponse", ModelConverters.getInstance()
                                .resolveAsResolvedSchema(new AnnotatedType(ErrorResponse.class))
                                .schema)
                )
                .info(new Info()
                        .title("eMSP REST API")
                        .description("REST API documentation for Electric Mobility Service Provider")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("eMSP Support Team")
                                .email("hanyahua@outlook.com")));
    }

    private Content createErrorResponseContent() {
        Schema<?> schema = new Schema<>();
        schema.$ref("#/components/schemas/ErrorResponse");
        return new Content()
                .addMediaType(
                        "application/json",
                        new MediaType().schema(schema)
                );
    }
}
