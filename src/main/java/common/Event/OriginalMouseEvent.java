package common.event;

import common.eventtype.MouseEventType;

public class OriginalMouseEvent {
	private final MouseEventType type;
	private final Object data;

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
