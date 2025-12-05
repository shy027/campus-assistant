/*
 * @Author: shy 1533103845@qq.com
 * @Date: 2025-11-13 11:21:42
 * @LastEditors: shy 1533103845@qq.com
 * @LastEditTime: 2025-11-20 11:34:16
 * @FilePath: \spring-ai-api\src\main\java\top\mqxu\ai\config\Knife4jConfig.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package top.shy.campusassistant.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j API 文档配置
 *
 * @author 15331
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("Authorization", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("Bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("Authorization"))
                .info(new Info()
                        .title("campus-assistant 接口文档")
                        .version("1.0.0")
                        .summary("campus-assistant 接口文档")
                        .description("campus-assistant 演示项目")
                        .contact(new Contact()
                                .name("shy")
                                .email("shy@gmail.com")));
    }

    @Bean
    public GroupedOpenApi myApi() {
        String[] paths = {"/**"};
        String[] packagedToMatch = {"top.shy.campusassistant.controller"};
        return GroupedOpenApi.builder()
                .group("1")
                .displayName("campus-assistant API")
                .pathsToMatch(paths)
                .packagesToScan(packagedToMatch).build();
    }
}
