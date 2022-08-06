package com.prac.jwtprac.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class JwtFilter extends GenericFilterBean { //:2:

	private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

	public static final String AUTHORIZATION_HEADER = "Authorization";

	private TokenProvider tokenProvider;

	public JwtFilter(TokenProvider tokenProvider) { // 토큰 프로바이더 주입
		this.tokenProvider = tokenProvider;
	}

	@Override // 토큰의 인증정보를 SecurityContext에 저장하는 역할
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
		String jwt = resolveToken(httpServletRequest); // 리퀘스트에서 토큰을 받아 JWT로 만듬
		String requestURI = httpServletRequest.getRequestURI();

		if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) { // validateToken : 토큰 유효성 검사
			Authentication authentication = tokenProvider.getAuthentication(jwt); // 토큰이 정상일 경우 토큰에서 authentication를 받음
			SecurityContextHolder.getContext().setAuthentication(authentication); // 받은 토큰을 SecurityContextHolder에 셋 해줌
			logger.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
		} else {
			logger.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
		}

		filterChain.doFilter(servletRequest, servletResponse);
	}

	private String resolveToken(HttpServletRequest request) { // request 해더에서 토큰 정보를 꺼내오는 역할
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
}
