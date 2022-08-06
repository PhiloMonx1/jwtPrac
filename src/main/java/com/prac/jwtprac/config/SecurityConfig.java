package com.prac.jwtprac.config;


import com.prac.jwtprac.jwt.JwtAccessDeniedHandler;
import com.prac.jwtprac.jwt.JwtAuthenticationEntryPoint;
import com.prac.jwtprac.jwt.JwtSecurityConfig;
import com.prac.jwtprac.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity // 기본적인 웹 보안을 활성화 하겠다는 어노테이션 = 시큐리티라면 붙어야 함
@EnableGlobalMethodSecurity(prePostEnabled = true) // PreAuthorize 어노테이션을 메소드 단위로 사용하기 위해서 만듬 :메소드 단위 사용?:
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private final TokenProvider tokenProvider;
//	private final CorsFilter corsFilter;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

	public SecurityConfig( // jwt 폴더 안에 만든 것들을 주입 받음
			TokenProvider tokenProvider,
//			CorsFilter corsFilter,
			JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, // 401에러
			JwtAccessDeniedHandler jwtAccessDeniedHandler // 403 에러
	) {
		this.tokenProvider = tokenProvider;
//		this.corsFilter = corsFilter;
		this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
		this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(); // 사용자 비밀번호를 암호화 하기 위해서 : 사용은 따로 해줘야 하지만 여기서 선언해야 사용할 수 있다.
	}

	@Override
	public void configure(WebSecurity web) { // h2 콘솔 사용할래
		web.ignoring()
				.antMatchers(
						"/h2-console/**"
						,"/favicon.ico"
						,"/error"
				);
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				// token을 사용하는 방식이기 때문에 csrf를 disable합니다.
				.csrf().disable()

//				.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)

				.exceptionHandling()
				.authenticationEntryPoint(jwtAuthenticationEntryPoint) // 예외처리를 위해 하는가 봄 authenticationEntryPoint가 뭔지 찾아야 함
				.accessDeniedHandler(jwtAccessDeniedHandler) // 위와 마찬가지 이외의 다른 메서드로 다른 예외처리가 가능한지도 알아볼 것

				// enable h2-console
				.and()
				.headers()
				.frameOptions()
				.sameOrigin()

				// 세션을 사용하지 않기 때문에 STATELESS로 설정
				.and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)

				.and()
				.authorizeRequests()// 모든 요청에 접근재한 걸기
				.antMatchers("/api/hello").permitAll()
				.antMatchers("/api/authenticate").permitAll() // 토큰 받기 위한 로그인 API 열어준다.
				.antMatchers("/api/signup").permitAll() // 회원가입 API 열어준다

				.anyRequest().authenticated() //아무 요청에.인증이 필요함을 걸겠다.

				.and()
				.apply(new JwtSecurityConfig(tokenProvider)); // jwt시큐리티 설정 파일도 적용
	}
}