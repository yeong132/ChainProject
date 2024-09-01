package org.zerock.chain.jy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 모든 파일을 C:/upload/ 폴더에서 서빙하도록 설정
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:///C:/upload/");

        // 메일 이미지 경로도 C:/upload/로 설정
        registry.addResourceHandler("/assets/img/mailimg/**")
                .addResourceLocations("file:///C:/upload/");
    }
}
