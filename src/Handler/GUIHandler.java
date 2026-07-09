package Handler;

import Event.GUIEvent;
import EventType.AppType;
import EventType.GUIEventType;
import EventType.WallType;
import Listener.GUIEventListener;
import main.Main;

public class GUIHandler {
	private Main main;
	private GUIEventListener listener;

	public void setEventListener(Main main, GUIEventListener listener) {
		this.main = main;
		this.listener = listener;
	}

	public void client() {
		main.setAppType(AppType.CLIENT);
	}

	public void server() {
		main.setAppType(AppType.SERVER);
	}

	public void stopServer() {
		listener.onGUIEvent(new GUIEvent(GUIEventType.STOPSERVER, null));
	}

	public void retry() {
		listener.onGUIEvent(new GUIEvent(GUIEventType.RETRY, null));
	}

	public void choiceNorthWall() {
		listener.onGUIEvent(new GUIEvent(GUIEventType.CHOICEWLL, WallType.NORTH));
	}

	public void choiceSouthWall() {
		listener.onGUIEvent(new GUIEvent(GUIEventType.CHOICEWLL, WallType.SOUTH));
	}

	public void choiceWestWall() {
		listener.onGUIEvent(new GUIEvent(GUIEventType.CHOICEWLL, WallType.WEST));
	}

	public void choiceEastWall() {
		listener.onGUIEvent(new GUIEvent(GUIEventType.CHOICEWLL, WallType.EAST));
	}

    public void pushedSuccessOkButton() {
		listener.onGUIEvent(new GUIEvent(GUIEventType.PUSHSUCCESSOK,null));
	}

	public void moveWheel(int amount) {
		listener.onGUIEvent(new GUIEvent(GUIEventType.MOVEWHEEL, amount));
	}

	public void clickLeftMouse(boolean pressed) {
		listener.onGUIEvent(new GUIEvent(GUIEventType.LEFTCLICK, pressed));
	}

	public void clickWheelMouse(boolean pressed) {
		listener.onGUIEvent(new GUIEvent(GUIEventType.WHEELCLICK, pressed));
	}

	public void clickRightMouse(boolean pressed) {
		listener.onGUIEvent(new GUIEvent(GUIEventType.RIGHTCLICK, pressed));
	}
}
