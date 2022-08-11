package com.prac.jwtprac.security.jwt;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component // :5: 이건 4번과 같은 느낌인데 이게 꼭 필요한지 4번과 합칠 수 없는지 알아보자 4번 = JwtAuthenticationEntryPoint
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
		//필요한 권한이 없이 접근하려 할때 403
		response.sendError(HttpServletResponse.SC_FORBIDDEN);
	}
}
