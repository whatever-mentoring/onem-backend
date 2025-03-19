package community.whatever.onembackendjava;

import org.junit.jupiter.api.Disabled;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * 컨테이너를 특정 테스트에서만 사용하고 싶을때, 이 추상클래스를 extends 할 수 있다.
 */
@Disabled
@Testcontainers
public abstract class PostgresTestContainerSupport {
	//
	// @Container
	// private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
	// 	new PostgreSQLContainer<>("postgres:latest")
	// 		.withInitScript("schema.sql");
	//
	// @DynamicPropertySource
	// public static void overrideProps(DynamicPropertyRegistry registry) {
	// 	registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
	// 	System.out.println(POSTGRESQL_CONTAINER.getJdbcUrl());
	// 	registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
	// 	registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
	// }
}
