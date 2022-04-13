package com.example.security.jwt.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

// 추가적인 시큐리티 설정을 위해 WebSecurityConfigurer를 implements 하거나 WebSecurityConfigurerAdapter를 extends하는 방법이 있는데
// 여기서는 WebSecurityConfigurerAdapter를 extends 하는 방법 사용
@EnableWebSecurity // 기본적인 웹보안을 활성화하겠다.
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) {
        // embedded h2 console 접근에 원활히 하기 위해 h2 콘솔 하위 요청과, favicon은 시큐리티 적용하지않음
        //
        web
                .ignoring()
                .antMatchers(
                        "/h2/**",
                        "/favicon.ico"
                );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests() // HttpServletRequest를 사용하는 요청에 대한 접근 제한을 설정하겠다.
                .antMatchers("/api/hello").permitAll() // /api/hello 경로에 대해서는 인증없이 접근 허용
                .anyRequest().authenticated(); // 나머지 요청에 대해서는 인증을 받아야한다.
    }
}
