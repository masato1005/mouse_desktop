package Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import EventType.DataType;

public class InputConvertedData {
	private static final ObjectMapper mapper = new ObjectMapper();

	private DataType dataType;
	private JsonNode data;	

	public InputConvertedData() {
	}

	public InputConvertedData(DataType dataType, Object data) {
		this.dataType = dataType;
		this.data = mapper.valueToTree(data);
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public JsonNode getData() {
		return data;
	}

	public void setData(JsonNode data) {
		this.data = data;
	}

}
