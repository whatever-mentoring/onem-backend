package community.whatever.onembackendjava.common.error.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    DB001("DB001","잘못된 요청입니다."),
    DB002("DB002","데이터를 찾을 수 없습니다."),
    DB003("DB003","참조키를 찾을 수 없습니다.");

    private final String errorCode;
    private final String message;
}
