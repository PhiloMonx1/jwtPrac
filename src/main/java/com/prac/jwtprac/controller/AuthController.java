package com.prac.jwtprac.controller;

import com.prac.jwtprac.dto.LoginDto;
import com.prac.jwtprac.dto.TokenDto;
import com.prac.jwtprac.jwt.JwtFilter;
import com.prac.jwtprac.jwt.TokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {
	private final TokenProvider tokenProvider;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;

	public AuthController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
		this.tokenProvider = tokenProvider;
		this.authenticationManagerBuilder = authenticationManagerBuilder;
	}

	@PostMapping("/authenticate") // 로그인 api
	public ResponseEntity<TokenDto> authorize(@Valid @RequestBody LoginDto loginDto) { // 로그인 DTO를 받는다

		UsernamePasswordAuthenticationToken authenticationToken = // authenticationToken을 만듬
				new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()); // 유저네임 패스워드 뽑음

		// 바로 아래가 실행될 때 CustomUserDetailsService에서 만든 loadUserByUsername 메서드가 실행됨
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);// 위에서 만든 토큰! // 이게 실행될 때 CustomUserDetailsService에서 만든 loadUserByUsername 메서드가 실행됨
		SecurityContextHolder.getContext().setAuthentication(authentication);// 바로 위에서 만든 객체 SecurityContext 시큐리티 컨텍스트에 저장
		/*SecurityContext 저장은 왜 하는걸까?? 무슨 효과가 있지?*/
		String jwt = tokenProvider.createToken(authentication); // 위에서 만든 객체로 JWT 토큰을 생성

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt); // 토큰을 헤더에 넣어줌

		return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK); // 바디에도 넣어줌 // 이때 토큰DTO로 넣어줌
	}
}