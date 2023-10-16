package com.bit.eduventure.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

//    @Value("${file.resource}")
//
//    private String filePath;

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry resourceHandlerRegistry){
//
//        resourceHandlerRegistry.addResourceHandler("/upload/**").addResourceLocations(filePath);
//
//
//    }


    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
                //예외로 동작할 주소 지정
                .allowedOrigins("http://localhost:3000")
                //허용될 요청방식
                .allowedMethods("*")
                //허용될 요청 헤더
                .allowedHeaders("*")
                //인증에 관한 정보 허용
                .allowCredentials(true)
                //타임아웃 시간 설정
                .maxAge(3600);
    }

}
