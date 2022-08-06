package com.prac.jwtprac.service;

import java.util.Collections;
import java.util.Optional;

import com.prac.jwtprac.dto.UserDto;
import com.prac.jwtprac.entity.Authority;
import com.prac.jwtprac.entity.User;
import com.prac.jwtprac.repository.UserRepository;
import com.prac.jwtprac.util.SecurityUtil;
import javassist.bytecode.DuplicateMemberException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public UserDto signup(UserDto userDto) { // 회원가입 로직 유저 DTO에서 네임을 뽑아서 검증한다.
		if (userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null) != null) {
			throw new RuntimeException("이미 가입되어 있는 유저입니다.");
		}

		Authority authority = Authority.builder() // 중복이 안되면 우선 권한 정보를 만든다.
				.authorityName("ROLE_USER")
				.build();

		User user = User.builder()
				.username(userDto.getUsername())
				.password(passwordEncoder.encode(userDto.getPassword()))
				.nickname(userDto.getNickname())
				.authorities(Collections.singleton(authority))//권한 정보를 넣어서 유저를 만든다.
				.activated(true)
				.build();

		return UserDto.from(userRepository.save(user));// 세이브한다.
	}

	@Transactional(readOnly = true) // 유저와 권한 정보를 리턴
	public UserDto getUserWithAuthorities(String username) { // username을 기준으로 유저와 권한 정보를 리턴
		return UserDto.from(userRepository.findOneWithAuthoritiesByUsername(username).orElse(null));
	}

	@Transactional(readOnly = true) // 유저와 권한 정보를 리턴
	public UserDto getMyUserWithAuthorities() { //유저네임 기준은 동일하나 현재 시큐리티 컨텍스트에 저장된 애만
		return UserDto.from(SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByUsername).orElse(null));
	}// 위의 두 메서드는 허용권한을 다르게 해서 권한 검증에 쓰이기 위함이다.
}
