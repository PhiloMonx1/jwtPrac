package com.prac.jwtprac.security.jwt;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//JwtSecurityConfig = :3: JWT필터랑 Token프로바이더를 SecurityConfig과 연결해줌
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

	private TokenProvider tokenProvider;

	public JwtSecurityConfig(TokenProvider tokenProvider) { // 토큰 프로바이더 주입받음
		this.tokenProvider = tokenProvider;
	}

	@Override
	public void configure(HttpSecurity http) {
		JwtFilter customFilter = new JwtFilter(tokenProvider); // JWT 필터
		http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class); // 시큐리치 로직에 위에서 뽑은 필터 등록함
	}
}