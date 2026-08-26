package Mouse;

import EventType.MouseEventType;
import EventType.WallType;
import Handler.MouseHandler;
import Json.Modifiers;
import Json.MouseData;
import Listener.implemented.ImplementedMouseListener;
import Mouse.Contents.MouseDrawer;
import Mouse.Contents.MouseScanner;

public class MouseManager {
	private final ImplementedMouseListener listener = new ImplementedMouseListener();

	private final MouseScanner mouseScanner = new MouseScanner(this, listener);
	private final MouseDrawer mouseDrawer = new MouseDrawer(this,listener);
	private MouseHandler handler;

	public void start() {
		mouseScanner.start();
	}

	public void mouseMove(MouseData mouseData) {
		listener.mouseMoved(mouseData);
	}

	public void moveWheel(int amount) {
		MouseData mouseData = new MouseData(
				MouseEventType.WHEELMOVE,
				0, 0, 0, 0,
				amount,
				false,
				new Modifiers());
		listener.mouseWheelMoved(mouseData);
	}

	public void clickMouse(MouseEventType type, boolean pressed) {
		MouseData mouseData = new MouseData(
				type,
				0, 0, 0, 0,
				0,
				pressed,
				new Modifiers());

		switch (type) {
			case LEFTCLICK -> listener.mouseLeftClicked(mouseData);
			case WHEELCLICK -> listener.mouseWheelClicked(mouseData);
			case RIGHTCLICK -> listener.mouseRightClicked(mouseData);
			default -> throw new IllegalArgumentException("クリック以外のイベントです: " + type);
		}
	}

	public void returnMouse(MouseData mouseData){
		listener.mouseMoved(mouseData);
	}

	public void openInvisibleWindow() {
		listener.openInvisibleWindow();
	}

	public void closeInvisibleWindow() {
		listener.closeInvisibleWindow();
	}

	public void setScannerWallType(WallType type) {
		mouseScanner.setWallType(type);
	}

	public void setListener(MouseHandler mouseHandler) {
		listener.setListener(mouseHandler);
	}

    public void updateDrawer(MouseData mouseData) {
        mouseDrawer.update(mouseData);
    }

    public void setDrawerWallType(WallType wallType) {
		mouseDrawer.setWallType(wallType);
    }

	public void returnMouseToClient(MouseData mouseData){
		mouseScanner.returnMouse(mouseData);
	}
}
