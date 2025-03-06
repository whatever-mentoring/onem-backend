package community.whatever.onembackendjava.common.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import community.whatever.onembackendjava.common.util.model.ResultJson;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

public class ResponseFormatter {

    public static ResponseEntity<ResultJson> ConvertResponse() {
        return ResponseEntity.ok(ResultJson.builder().build());
    }

    public static ResponseEntity<ResultJson> ConvertResponse(Object result) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        HashMap<String, Object> convert = objectMapper.convertValue(result, HashMap.class);
        return ResponseEntity.ok(
                ResultJson.builder().data(convert).build());
    }
}
