package com.prac.jwtprac.security.jwt;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component // :4: 자격없는 애 401에러 내주려고 만듬
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request,
	                     HttpServletResponse response,
	                     AuthenticationException authException) throws IOException {
		// 유효한 자격증명을 제공하지 않고 접근하려 할때 401
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED); // SC_UNAUTHORIZED 컨트롤 클릭하면 401에러라는 것을 볼 수 있다.
	}
}