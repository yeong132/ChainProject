package org.zerock.chain.jy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 기존 업로드 경로 설정
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:///C:/upload/");

        // 새로운 메일 이미지 경로 설정
        registry.addResourceHandler("/assets/img/mailimg/**")
                .addResourceLocations("classpath:/static/assets/img/mailimg/",
                        "file:src/main/resources/static/assets/img/mailimg/");
    }
}
