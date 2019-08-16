package com.otus.java.ioc.configuration;

import com.google.gson.Gson;
import domain.Address;
import domain.Phone;
import domain.User;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.stream.Stream;

@Configuration
@ComponentScan(basePackages = "com.otus.java.ioc")
@EnableWebMvc
@RequiredArgsConstructor
public class WebContextConfiguration implements WebMvcConfigurer {
    private final ApplicationContext applicationContext;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // when static resources are inside resources folder under WEB-INF
        // registry.addResourceHandler("/resources/**").addResourceLocations("/WEB-INF/resources/");

        // when static resources are inside static folder under webapp
        registry.addResourceHandler("/static/**").addResourceLocations("/static/");
    }

    @Bean
    public ThymeleafViewResolver viewResolver() {
        final var viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        viewResolver.setOrder(1);
        viewResolver.setCharacterEncoding("UTF-8");
        return viewResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        final var templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        return templateEngine;
    }

    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        final var templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(this.applicationContext);
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCacheable(true);
        templateResolver.setCharacterEncoding("UTF-8");
        return templateResolver;
    }

    @Bean
    public SessionFactory sessionFactory() {
        final var configuration = new org.hibernate.cfg.Configuration().configure("hibernate.cfg.xml");
        final var serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();
        final MetadataSources metadataSources = new MetadataSources(serviceRegistry);
        Stream.of(Address.class, Phone.class, User.class).forEach(metadataSources::addAnnotatedClass);
        final Metadata metadata = metadataSources.getMetadataBuilder().build();
        return metadata.getSessionFactoryBuilder().build();
    }

    @Bean
    public Gson gson() {
        return new Gson();
    }
}
