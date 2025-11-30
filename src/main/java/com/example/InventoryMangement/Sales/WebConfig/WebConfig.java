package com.example.InventoryMangement.Sales.WebConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override

    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        registry.addResourceHandler("/invoices/**")
                .addResourceLocations("file:C:/Desktop/Start_Code/InventoryMangement/invoices/");
    }
}


