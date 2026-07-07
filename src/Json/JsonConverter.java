package Json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import EventType.DataType;

public class JsonConverter {
	private final ObjectMapper mapper = new ObjectMapper();

	public String dataConverter(DataType dataType, Object data) {
		InputConvertedData sendData = new InputConvertedData(dataType, data);
		try {
			return mapper.writeValueAsString(sendData);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

}
