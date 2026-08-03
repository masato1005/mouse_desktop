package Event;

import EventType.NetworkEventType;

public class NetworkEvent {
	private NetworkEventType type;
	private Object data;
	
	public NetworkEvent(NetworkEventType type, Object data){
		this.type = type;
		this.data = data;
	}
	
	public NetworkEventType getType() {
		return type;
	}
	
	public Object getData() {
		return data;
	}

}
