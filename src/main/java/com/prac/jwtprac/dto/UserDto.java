package com.prac.jwtprac.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.prac.jwtprac.entity.User;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

	@NotNull
	@Size(min = 3, max = 50)
	private String username;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // 제이슨 프로퍼티?? 쓰기만 허용?? // 패스워드는 읽으면 안돼서 쓰기만 허용하는 것일지도...
	@NotNull
	@Size(min = 3, max = 100)
	private String password;

	@NotNull
	@Size(min = 3, max = 50)
	private String nickname;

	private Set<AuthorityDto> authorityDtoSet; // UserDto from 및 Set<AuthorityDto>는 설명이 없다. 더 공부해볼 것

	public static UserDto from(User user) {
		if(user == null) return null;

		return UserDto.builder()
				.username(user.getUsername())
				.nickname(user.getNickname())
				.authorityDtoSet(user.getAuthorities().stream()
						.map(authority -> AuthorityDto.builder().authorityName(authority.getAuthorityName()).build())
						.collect(Collectors.toSet()))
				.build();
	}
}