//package com.cqupt.art.auth;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.security.web.firewall.HttpFirewall;
//import org.springframework.security.web.firewall.StrictHttpFirewall;
//
////@Configuration
////@EnableWebSecurity
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//    @Autowired
//    MyAuthenticationSuccessHandler successHandler;
//
//    @Autowired
//    MyAuthenticationFailureHandler failureHandler;
//
//    @Autowired
//    UserDetailsService userDetailsService;
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.addFilterBefore(loginFilter(), UsernamePasswordAuthenticationFilter.class);
//
//        http.authorizeRequests()  //开启配置
//                // 验证码、登录接口放行
//                .antMatchers("/verify-code","/auth/login").permitAll()
//                .anyRequest() //其他请求
//                .authenticated().and()//验证   表示其他请求需要登录才能访问
//                .csrf().disable();  // 禁用 csrf 保护
//    }
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService)
//                .passwordEncoder(passwordEncoder());
//    }
//    @Bean
//    public PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
//
//    @Override
//    public void configure(WebSecurity webSecurity){
//        webSecurity.ignoring().antMatchers("/css/**","/fonts/**","/img/**","/js/**");
//    }
//
//    @Bean
//    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
//        StrictHttpFirewall firewall = new StrictHttpFirewall();
//        //此处可添加别的规则,目前只设置 允许双 //
//        firewall.setAllowUrlEncodedDoubleSlash(true);
//        return firewall;
//    }
//
//    @Bean
//    LoginFilter loginFilter() throws Exception {
//        LoginFilter loginFilter = new LoginFilter();
//        loginFilter.setFilterProcessesUrl("/auth/login");
//        loginFilter.setUsernameParameter("phone");
//        loginFilter.setPasswordParameter("pwd");
//        loginFilter.setAuthenticationManager(authenticationManagerBean());
//        loginFilter.setAuthenticationSuccessHandler(new MyAuthenticationSuccessHandler());
//        loginFilter.setAuthenticationFailureHandler(new MyAuthenticationFailureHandler());
//        return loginFilter;
//    }
//}
