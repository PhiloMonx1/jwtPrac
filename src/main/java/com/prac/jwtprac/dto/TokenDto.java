package com.prac.jwtprac.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenDto { // 토큰 정보를 리스폰할 때 쓸 것!!

	private String token;
}
