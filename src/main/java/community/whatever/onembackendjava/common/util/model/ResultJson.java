package community.whatever.onembackendjava.common.util.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;


@Data
@Builder
@AllArgsConstructor
public class ResultJson implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1206151647868602861L;

	protected ResultJson(){}
	
    @Builder.Default 
    String resultCode = "1000";
    @Builder.Default 
    String msg = "OK";

    @Builder.Default 
    HashMap<String, Object> data = new HashMap<String, Object>();

    @Builder.Default 
    TimeZone timeZone = TimeZone.getDefault();

    @Builder.Default
    Long timeStamp = System.currentTimeMillis();

}
