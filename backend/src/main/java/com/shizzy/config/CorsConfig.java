//package com.shizzy.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//
//@Configuration
//public class CorsConfig {
//    @Bean
//    public CorsFilter corsFilter() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//        config.addAllowedOrigin("^http?:\\/\\/link~~~.com$"); // e.g. /**, http://domain1.com
//        config.addAllowedHeader(CorsConfiguration.ALL);
//        config.addAllowedMethod(HttpMethod.GET);
//        config.addAllowedMethod(HttpMethod.POST);
//        config.addAllowedMethod(HttpMethod.HEAD);
//        config.addAllowedMethod(HttpMethod.PUT);
//        config.addAllowedMethod(HttpMethod.DELETE);
//        config.addAllowedMethod(HttpMethod.TRACE);
//        config.addAllowedMethod(HttpMethod.OPTIONS);
//        config.setAllowCredentials(true);
//        config.setMaxAge(3600L);
//
//        source.registerCorsConfiguration("^http?:\\/\\/link~~~.com$", config); // "/**"
//        return new CorsFilter(source);
//    }
//}
