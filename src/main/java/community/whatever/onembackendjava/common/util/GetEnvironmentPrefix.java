package community.whatever.onembackendjava.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetEnvironmentPrefix {

    @Value("${app.environment:}")
    private static String environment;

    /*
     * 현재 환경의 prefix를 반환합니다.
     */
    public static String getEnvPrefix() {
        return determinePrefix(environment);
    }
    /*
     * 환경에 따른 prefix를 결정합니다.
     */
public static String determinePrefix(String environment) {
    return switch (environment.toLowerCase()) {
        case "development" -> "dev";
        case "staging" -> "stg";
        case "production" -> "prd";
        default -> throw new IllegalArgumentException("지원하지 않는 환경: " + environment);
    };
}

}