package Handler;

import Event.OriginalMouseEvent;
import EventType.MouseEventType;
import Listener.MouseEventListener;

public class MouseHandler {
	private MouseEventListener listener;

	public void setEventListener(MouseEventListener listener) {
		this.listener = listener;
	}

	public void mouseMoved(String json) {
		listener.onMouseEvent(new OriginalMouseEvent(MouseEventType.MOVE, json));
	}

    public void openInvisibleWindow() {
		listener.onMouseEvent(new OriginalMouseEvent(MouseEventType.SENDMOUSE,null));
    }

    public void closeInvisibleWindow() {
		listener.onMouseEvent(new OriginalMouseEvent(MouseEventType.CLOSEINVISIBLEWINDOW, null));
    }
}
