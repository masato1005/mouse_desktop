package Json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import EventType.DataType;
import gui.contents.ErrorExitGUI;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class JsonConverter {
	private final ObjectMapper mapper = new ObjectMapper();

	public String dataConverter(DataType dataType, Object data) {
		InputConvertedData sendData = new InputConvertedData(dataType, data);
		try {
			return mapper.writeValueAsString(sendData);
		} catch (JsonProcessingException e) {
			new ErrorExitGUI("Json処理で不具合が発生しました");
			return null;
		}
	}
}
