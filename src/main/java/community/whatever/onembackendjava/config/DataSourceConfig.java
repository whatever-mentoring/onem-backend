package community.whatever.onembackendjava.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * 데이터베이스 연결 설정
 * - 주 데이터베이스(Primary): 쓰기 작업용
 * - 레플리카 데이터베이스(Replica): 읽기 작업용
 */
@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {

    /**
     * 주 데이터베이스 속성 설정
     */
    @Bean
    @ConfigurationProperties("spring.datasource.primary")
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * 레플리카 데이터베이스 속성 설정
     */
    @Bean
    @ConfigurationProperties("spring.datasource.replica")
    public DataSourceProperties replicaDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * 주 데이터베이스 데이터 소스 (쓰기 작업용)
     */
    @Bean
    @Primary
    public DataSource primaryDataSource() {
        return primaryDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    /**
     * 레플리카 데이터베이스 데이터 소스 (읽기 작업용)
     */
    @Bean
    public DataSource replicaDataSource() {
        return replicaDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    /**
     * 주 데이터베이스용 JDBC 템플릿 (쓰기 작업용)
     */
    @Bean
    @Primary
    public JdbcTemplate primaryJdbcTemplate(DataSource primaryDataSource) {
        return new JdbcTemplate(primaryDataSource);
    }

    /**
     * 레플리카 데이터베이스용 JDBC 템플릿 (읽기 작업용)
     */
    @Bean
    public JdbcTemplate replicaJdbcTemplate(DataSource replicaDataSource) {
        return new JdbcTemplate(replicaDataSource);
    }
}
