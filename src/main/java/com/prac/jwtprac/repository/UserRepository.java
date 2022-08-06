package com.prac.jwtprac.repository;

import com.prac.jwtprac.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> { //:6: 6번 전에 DTO를 만듬! 그리고 DTO전에 시큐리티콘픽에 설정을 추가해줌!
	@EntityGraph(attributePaths = "authorities")// 이걸 붙이면 쿼리가 실행될 때 Eager로 authorities(권한)를 같이 가져온다.
	Optional<User> findOneWithAuthoritiesByUsername(String username); // 유저 네임을 기준으로 하나를 찾는다. 이떼 With 권한 정보를 같이 가져온다.
}