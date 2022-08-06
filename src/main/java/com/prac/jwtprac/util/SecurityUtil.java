package com.prac.jwtprac.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class SecurityUtil { //:8: getCurrentUsername메서드 하나만을 가진 클래스

	private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

	private SecurityUtil() {
	}

	public static Optional<String> getCurrentUsername() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		//AuthController에서 SecurityContextHolder에 authentication를 적재한다.
		//하지만 실제 저장되는 시점은 jwt필터의 doFilter메서드가 실행될 때 내부에 포함되어 있다. 윗줄은 뭐지 그럼??
		//여튼 doFilter메서드가 안에서 저장된 객체가 이때 꺼내진다.
		if (authentication == null) {
			logger.debug("Security Context에 인증 정보가 없습니다.");
			return Optional.empty();
		}

		String username = null;
		if (authentication.getPrincipal() instanceof UserDetails) {
			UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
			username = springSecurityUser.getUsername();
		} else if (authentication.getPrincipal() instanceof String) {
			username = (String) authentication.getPrincipal();
		}

		return Optional.ofNullable(username); // 결국 검증을 통해 유저네임을 리턴해준다.
	}
}