package com.example.codemind.app;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@Slf4j
@Configuration
@EnableSwagger2WebMvc
// 对JSR303提供支持
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig {

    @Value("${spring.application.name}")
    private String applicationName;
    @Bean
    public Docket defaultApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName(applicationName)
                .select()
                // 添加@Api注解才显示
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                // 这里指定Controller扫描包路径
//                .apis(RequestHandlerSelectors.basePackage("com.yolo"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * swagger-api接口描述信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("API文档")
                .description("API文档")
                .contact(
                        new Contact(
                                "ouyangkang",
                                "",
                                ""
                        )
                )
                .version("1.0.0")
                .build();
    }
}