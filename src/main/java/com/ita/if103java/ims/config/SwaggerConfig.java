package com.ita.if103java.ims.config;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.ita.if103java.ims.security.UserDetailsImpl;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
@ComponentScan(basePackages = "com.ita.if103java.ims")
public class SwaggerConfig {

    @Value("${swagger.security.headerName}")
    private String HEADER_NAME;     // Authorization

    @Value("${swagger.urls.secureUrl}")
    private String SECURE_URL;      // /.*

    @Value("#{new Boolean('${swagger.displayRequestDuration}')}")
    private Boolean displayRequestDuration;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .ignoredParameterTypes(UserDetailsImpl.class)
            .directModelSubstitute(Pageable.class, SwaggerPageable.class)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .paths(Predicates.not(PathSelectors.regex("/error.*")))
            .build()
            .apiInfo(metadata())
            .securitySchemes(Lists.newArrayList(apiKey()))
            .securityContexts(Lists.newArrayList(securityContext()));
    }

    @Bean
    public UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
            .displayRequestDuration(displayRequestDuration)
            .build();
    }

    private ApiInfo metadata() {
        return new ApiInfoBuilder()
            .title("Inventory Management System")
            .description("It is a web application that helps to coordinate and manage work of different organizations" +
                "(organize all products, spare parts, items) within a warehouse or home. \n There is represented dashboard " +
                "where user can see capacity of each warehouse and its load.")
            .version("Demo 2")
            .license("Our team account on GitHub")
            .licenseUrl("https://github.com/IF-103-Java")
            .build();
    }

    private ApiKey apiKey() {
        return new ApiKey("JWT", HEADER_NAME, "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
            .securityReferences(defaultAuth())
            .forPaths(regex(SECURE_URL))
            .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
            = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(
            new SecurityReference("JWT", authorizationScopes));
    }

    private static class SwaggerPageable {
        @ApiModelProperty(value = "Number of records per page", example = "20")
        private int size;

        @ApiModelProperty(value = "Results page you want to retrieve (0..N)", example = "0")
        private int page;

        @ApiModelProperty("Sorting criteria in the format: property(,asc|desc)." +
            "Default sort order is ascending. Multiple sort criteria are supported.")
        private List<String> sort;

        public SwaggerPageable() {
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public List<String> getSort() {
            return sort;
        }

        public void setSort(List<String> sort) {
            this.sort = sort;
        }
    }
}
