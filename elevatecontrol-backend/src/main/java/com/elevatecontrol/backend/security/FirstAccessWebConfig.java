package com.elevatecontrol.backend.security;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
@Configuration
public class FirstAccessWebConfig implements WebMvcConfigurer {
 private final FirstAccessInterceptor interceptor;
 public FirstAccessWebConfig(FirstAccessInterceptor i){this.interceptor=i;}
 @Override public void addInterceptors(InterceptorRegistry registry){
   registry.addInterceptor(interceptor).addPathPatterns("/api/**")
     .excludePathPatterns("/api/auth/login","/api/auth/logout","/api/auth/me","/api/auth/csrf",
       "/api/auth/change-password","/api/auth/complete-setup","/api/auth/forgot-password","/api/auth/reset-password");
 }
}
