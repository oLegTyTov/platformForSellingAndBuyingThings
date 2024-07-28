package com.example.platformaccountsproducts.configs;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
public class WebConfigLocalization implements WebMvcConfigurer {

    @Bean
    public LocaleResolver localeResolver() {
        // Використання SessionLocaleResolver для зберігання локалі у сесії
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        sessionLocaleResolver.setDefaultLocale(Locale.ENGLISH);
        return sessionLocaleResolver;

        // Використання CookieLocaleResolver для зберігання локалі у куках
        // CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        // cookieLocaleResolver.setDefaultLocale(Locale.ENGLISH);
        // cookieLocaleResolver.setCookieName("localeCookie");
        // cookieLocaleResolver.setCookieMaxAge(4800);
        // return cookieLocaleResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:messages"); // Вказуйте базове ім'я файлів властивостей
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
