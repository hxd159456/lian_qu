package com.cqupt.art.seckill.config;


import com.cqupt.art.seckill.intecpter.AccessLimitIntercptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public AccessLimitIntercptor getAccessLimitIntercptor(){
        return new AccessLimitIntercptor();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new LoginInterceptor())
//                .addPathPatterns("/app/**");

        registry.addInterceptor(getAccessLimitIntercptor())
                .addPathPatterns("/app/seckill/nft");
    }

    //跨域设置
    //顺序：servlet的filter->拦截器->mapping，使用自定义拦截器时会导致跨域失败
    //使用CorsFilter解决
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        log.info("addCorsMappings");
//        registry.addMapping("/**")
//                .allowedOrigins("*")
//                .allowCredentials(true)
//                .allowedMethods("GET","POST","HEAD","DELETE","OPTIONS")
//                .maxAge(3600)
//                .allowedHeaders("*");
//    }


}
