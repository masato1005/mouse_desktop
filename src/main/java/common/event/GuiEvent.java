package common.event;

import common.eventtype.GuiEventType;

public class GuiEvent {
	private final GuiEventType type;
	private final Object data;
	
	public GuiEvent(GuiEventType type, Object data) {
		this.type = type;
		this.data = data;
	}
	
	public GuiEventType getType() {
		return type;
	}
	
	public Object getData() {
		return data;
	}
}
