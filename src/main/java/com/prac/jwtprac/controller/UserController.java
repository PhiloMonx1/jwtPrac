package com.prac.jwtprac.controller;


import com.prac.jwtprac.dto.UserDto;
import com.prac.jwtprac.entity.User;
import com.prac.jwtprac.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/hello")
	public ResponseEntity<String> hello() {
		return ResponseEntity.ok("hello");
	}

	@PostMapping("/test-redirect")
	public void testRedirect(HttpServletResponse response) throws IOException {
		response.sendRedirect("/api/user");
	}

	@PostMapping("/signup")
	public ResponseEntity<UserDto> signup(@Valid @RequestBody UserDto userDto) {// 회원가입 @Valid는 워징?
		return ResponseEntity.ok(userService.signup(userDto));
	}

	@GetMapping("/user")
	@PreAuthorize("hasAnyRole('USER','ADMIN')") // PreAuthorize를 통해서 호출 권한을 지정할 수 있다.
	public ResponseEntity<UserDto> getMyUserInfo() {
		return ResponseEntity.ok(userService.getMyUserWithAuthorities());
	}

	@GetMapping("/user/{username}")
	@PreAuthorize("hasAnyRole('ADMIN')") // 해당 api는 어드민만 호출 가능하다.
	public ResponseEntity<UserDto> getUserInfo(@PathVariable String username) {
		return ResponseEntity.ok(userService.getUserWithAuthorities(username));
	}
}