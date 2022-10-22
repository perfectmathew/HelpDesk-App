package com.perfect.hepdeskapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class HepDeskAppApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(HepDeskAppApplication.class, args);
	}
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		registry.addResourceHandler("/uploads/**").addResourceLocations("/WEB-INF/uploads/")
				.setCacheControl(CacheControl.maxAge(2, TimeUnit.HOURS).cachePublic());
		WebMvcConfigurer.super.addResourceHandlers(registry);
		registry.addResourceHandler("/resources/**").addResourceLocations("/WEB-INF/resources/");

	}
}
