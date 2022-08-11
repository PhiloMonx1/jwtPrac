package com.prac.jwtprac.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TokenProvider implements InitializingBean {// :1: 토큰의 생성과 토큰 유효성 검사를 위한 클래스 "afterPropertiesSet"를 오버라이드 하기 위해 상속
	private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

	private static final String AUTHORITIES_KEY = "auth";

	private final String secret;
	private final long tokenValidityInMilliseconds;

	private Key key;


	public TokenProvider(
			@Value("${jwt.secret}") String secret,
			@Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds) {
		this.secret = secret; // 밑에서 디코드를 해서 키에 할당한다.
		this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
	}

	@Override
	public void afterPropertiesSet() { // 빈 생성 -> DI -> 주입 받은 시크릿 값 -> 디코드 -> kye에 할당
		byte[] keyBytes = Decoders.BASE64.decode(secret);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	public String createToken(Authentication authentication) { // authentication의 권한정보를 이용해서 토큰을 생성
		String authorities = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(",")); // authentication을 받아서 authorities권한을 만드는 것을 볼 수 있다.

		long now = (new Date()).getTime();
		Date validity = new Date(now + this.tokenValidityInMilliseconds); // tokenValidityInMilliseconds은 yml파일에서 설정한 시간이 들어간다. 86400

		return Jwts.builder() // 위의 정보를 담아서 JWT토큰을 빌드
				.setSubject(authentication.getName())// authentication에서 겟네임
				.claim(AUTHORITIES_KEY, authorities)
				.signWith(key, SignatureAlgorithm.HS512)
				.setExpiration(validity)//시간이 들어감
				.compact();
	}

	public Authentication getAuthentication(String token) { // 토큰을 파라미터로 받는다.
		Claims claims = Jwts // 그걸 토대로 클레임을 만든다. 클레임? : 사용자에 대한 프로퍼티/속성
				.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();

		Collection<? extends GrantedAuthority> authorities =
				Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
						.map(SimpleGrantedAuthority::new)
						.collect(Collectors.toList()); // 클레임에서 권한을 뽑아낸다.

		User principal = new User(claims.getSubject(), "", authorities); // 뽑아낸 권한을 이용해 유저 객체를 만든다.

		return new UsernamePasswordAuthenticationToken(principal, token, authorities); // 유저 객체, 토큰, 권한정보를 리턴함
	}

	public boolean validateToken(String token) { // 토큰의 유효성을 검사하는 메서드
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token); // 받은 토큰을 파싱
			return true; // 문제 없으면 트루 아니면 밑에 캐치로 들어간다.
		} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
			logger.info("잘못된 JWT 서명입니다.");
		} catch (ExpiredJwtException e) {
			logger.info("만료된 JWT 토큰입니다.");
		} catch (UnsupportedJwtException e) {
			logger.info("지원되지 않는 JWT 토큰입니다.");
		} catch (IllegalArgumentException e) {
			logger.info("JWT 토큰이 잘못되었습니다.");
		}
		return false;
	}
}
