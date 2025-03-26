package community.whatever.onembackendjava.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class AbstractTestcontainersTest {
    
    static {
        // Docker 설정 (모든 관련 환경 변수 명시적 설정)
        System.setProperty("docker.host", "unix:///Users/jeongseongheon/.rd/docker.sock");
        System.setProperty("docker.sock", "/Users/jeongseongheon/.rd/docker.sock");
        System.setProperty("testcontainers.docker.socket", "/Users/jeongseongheon/.rd/docker.sock");
        
        // 컨테이너 재사용 활성화
        System.setProperty("testcontainers.reuse.enable", "true");
        
        // Ryuk 비활성화 (컨테이너 정리 서비스)
        System.setProperty("testcontainers.ryuk.disabled", "true");
        
        // Docker 클라이언트 전략 설정
        System.setProperty("docker.client.strategy", "org.testcontainers.dockerclient.UnixSocketClientProviderStrategy");
        
        // 로깅 활성화
        System.setProperty("testcontainers.logs.stderr", "true");
        System.setProperty("testcontainers.logs.stdout", "true");
    }
    
    protected static final PostgreSQLContainer<?> POSTGRES_CONTAINER;
    
    static {
        DockerImageName postgresImage = DockerImageName.parse("postgres:16-alpine")
                .asCompatibleSubstituteFor("postgres");
                
        POSTGRES_CONTAINER = new PostgreSQLContainer<>(postgresImage)
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withReuse(true);
        try {
            POSTGRES_CONTAINER.start();
            System.out.println("PostgreSQL 컨테이너 시작: " + POSTGRES_CONTAINER.getJdbcUrl());
        } catch (Exception e) {
            System.err.println("컨테이너 시작 실패: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        // 기본 데이터소스 설정
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        
        // primary 데이터소스 설정
        registry.add("spring.datasource.primary.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.primary.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.primary.password", POSTGRES_CONTAINER::getPassword);
        registry.add("spring.datasource.primary.driver-class-name", () -> "org.postgresql.Driver");
        
        // replica 데이터소스 설정
        registry.add("spring.datasource.replica.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.replica.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.replica.password", POSTGRES_CONTAINER::getPassword);
        registry.add("spring.datasource.replica.driver-class-name", () -> "org.postgresql.Driver");
    }
}
