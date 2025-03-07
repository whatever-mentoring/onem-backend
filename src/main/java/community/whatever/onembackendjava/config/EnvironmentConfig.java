package community.whatever.onembackendjava.config;

import community.whatever.onembackendjava.constant.AppEnvironment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvironmentConfig {

    @Value("${app.appEnvironment:DEV}")
    private String environmentName;

    @Bean
    public AppEnvironment appEnvironment() {
        return AppEnvironment.valueOf(environmentName.toUpperCase());
    }
}
