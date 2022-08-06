package com.prac.jwtprac.service;


import com.prac.jwtprac.entity.User;
import com.prac.jwtprac.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component("userDetailsService") // 스프링 시큐리티에서 제일 중요함!! 아래서 UserDetailsService를 상속
public class CustomUserDetailsService implements UserDetailsService {
	private final UserRepository userRepository;

	public CustomUserDetailsService(UserRepository userRepository) { // 유저 레포 주입!!
		this.userRepository = userRepository;
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(final String username) { // 로그인시 발동
		return userRepository.findOneWithAuthoritiesByUsername(username)// 로그인시 적은 유저네임 기준으로 레포에서 찾는다
				.map(user -> createUser(username, user)) // 그걸로 유저를 만든다!
				.orElseThrow(() -> new UsernameNotFoundException(username + " -> 데이터베이스에서 찾을 수 없습니다.")); // 없으면 못 만듬ㅋㅋ
	}

	private org.springframework.security.core.userdetails.User createUser(String username, User user) {// 위에서 만든 유저를 여기서 받음
		if (!user.isActivated()) { // 만약 받은 유저가 활성화 상태면 아래 리스트줄 쪽으로 넘어감
			throw new RuntimeException(username + " -> 활성화되어 있지 않습니다.");
		}
		List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
				.map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
				.collect(Collectors.toList()); // 유저한테서 권한을 뽑아내서 grantedAuthorities를 만든다. 이게 권한 정보
		return new org.springframework.security.core.userdetails.User( // 권한 정보, 유저네임, 패스워드를 가지고 유저 객체 리턴
				user.getUsername(),
				user.getPassword(),
				grantedAuthorities);
	}
}