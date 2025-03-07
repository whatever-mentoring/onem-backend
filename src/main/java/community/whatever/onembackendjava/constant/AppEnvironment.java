package community.whatever.onembackendjava.constant;

import lombok.Getter;

/**
 * 애플리케이션이 실행되는 환경을 나타내는 Enum
 */
@Getter
public enum AppEnvironment {
    DEV("dev"),
    STG("stg"),
    PRD("prd");

    private final String prefix;

    AppEnvironment(String prefix) {
        this.prefix = prefix;
    }

}
