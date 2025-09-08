package com.example.QuizApplicationImplemented.photoConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer{

    @Value("${file.product-upload-dir}")
    private String uploadDir;

    private void addResourceHandler(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/uploads/products/**")
                .addResourceLocations("file:" + Paths.get(uploadDir).toAbsolutePath() + "/")
                .setCachePeriod(0);
    }
}
