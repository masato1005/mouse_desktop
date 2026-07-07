package Event;

import EventType.GUIEventType;

public class GUIEvent {
	private GUIEventType type;
	private Object data;
	
	public GUIEvent(GUIEventType type, Object data) {
		this.type = type;
		this.data = data;
	}
	
	public GUIEventType getType() {
		return type;
	}
	
	public Object getData() {
		return data;
	}
}
