package community.whatever.onembackendjava.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetEnvironmentPrefix {

    @Value("${app.environment:}")
    public String environment;

    public String getEnvPrefix() {
        return determinePrefix(environment);
    }

    public String determinePrefix(String environment) {
    return switch (environment.toLowerCase()) {
        case "development" -> "dev";
        case "staging" -> "stg";
        case "production" -> "prd";
        default -> throw new IllegalArgumentException("지원하지 않는 환경: " + environment);
    };
}

}