package com.prac.jwtprac.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto { // 로그인 때 사용할 DTO

	@NotNull
	@Size(min = 3, max = 50) // @Size는 validation 관련 어노테이션이다. validation : 이번에 추가한 디펜던시 Lombok과 비슷한 역할일까?
	private String username;

	@NotNull
	@Size(min = 3, max = 100) // 역할은 스트링 길이 제한을 걸어주는 것 같다.
	private String password;
}