package community.whatever.onembackendjava.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class AbstractTestcontainersTest {
    
    static {
        // Rancher Desktop에서 사용하는 Docker 소켓 위치 지정
        System.setProperty("docker.host", "unix:///Users/jeongseongheon/.rd/docker.sock");
        // 컨테이너 재사용 활성화
        System.setProperty("testcontainers.reuse.enable", "true");
    }
    
    protected static final PostgreSQLContainer<?> POSTGRES_CONTAINER;
    
    static {
        POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:16-alpine")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withReuse(true);
        POSTGRES_CONTAINER.start();
    }
    
    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.primary.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.primary.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.primary.password", POSTGRES_CONTAINER::getPassword);
        registry.add("spring.datasource.replica.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.replica.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.replica.password", POSTGRES_CONTAINER::getPassword);
    }
}
