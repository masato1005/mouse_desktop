package common.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.eventtype.DataType;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public final class JsonConverter {
	private final static ObjectMapper mapper = new ObjectMapper();

	public static String toJson(DataType dataType, Object data) throws JsonProcessingException{
		InputConvertedData sendData = new InputConvertedData(dataType, data);
		try {
			return mapper.writeValueAsString(sendData);
		} catch (JsonProcessingException e) {
			throw e;
		}
	}
}
