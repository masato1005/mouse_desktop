package Event;

import EventType.MouseEventType;

public class OriginalMouseEvent {
	private MouseEventType type;
	private Object data;

	public OriginalMouseEvent(MouseEventType type, Object data) {
		this.type = type;
		this.data = data;
	}

	public MouseEventType getType() {
		return type;
	}

	public Object getData() {
		return data;
	}
}
