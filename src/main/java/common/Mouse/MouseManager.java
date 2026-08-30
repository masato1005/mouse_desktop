package common.Mouse;

import common.EventType.MouseEventType;
import common.EventType.WallType;
import common.Listener.MouseEventListener;
import common.Listener.implemented.ImplementedMouseListener;
import common.Mouse.Contents.MouseDrawer;
import common.Mouse.Contents.MouseScanner;
import common.data.Modifiers;
import common.data.MouseData;

public class MouseManager {
	private final ImplementedMouseListener listener = new ImplementedMouseListener();

	private final MouseScanner mouseScanner = new MouseScanner(this, listener);
	private final MouseDrawer mouseDrawer = new MouseDrawer(this,listener);

	public void setListener(MouseEventListener eventListener) {
		listener.setListener(eventListener);
	}

	public void setScannerWallType(WallType type) {
		mouseScanner.setWallType(type);
	}

    public void setDrawerWallType(WallType wallType) {
		mouseDrawer.setWallType(wallType);
    }

	public void start() {
		mouseScanner.start();
	}

	public void mouseMove(MouseData mouseData) {
		listener.mouseMoved(mouseData);
	}

	public void clickMouse(MouseEventType type, boolean pressed) {
		MouseData mouseData = new MouseData(
				type,
				0, 0, 0, 0,
				0,
				pressed,
				new Modifiers());

		switch (type) {
			case LEFT_CLICK -> listener.mouseLeftClicked(mouseData);
			case WHEEL_CLICK -> listener.mouseWheelClicked(mouseData);
			case RIGHT_CLICK -> listener.mouseRightClicked(mouseData);
			default -> throw new IllegalArgumentException("クリック以外のイベントです: " + type);
		}
	}

	public void moveWheel(int amount) {
		MouseData mouseData = new MouseData(
				MouseEventType.WHEEL_MOVE,
				0, 0, 0, 0,
				amount,
				false,
				new Modifiers());
		listener.mouseWheelMoved(mouseData);
	}

    public void updateDrawer(MouseData mouseData) {
        mouseDrawer.update(mouseData);
    }

	public void returnMouse(MouseData mouseData){
		listener.mouseMoved(mouseData);
	}

	public void returnMouseToClient(MouseData mouseData){
		mouseScanner.returnMouse(mouseData);
	}

	public void openInvisibleWindow() {
		listener.openInvisibleWindow();
	}

	public void closeInvisibleWindow() {
		listener.closeInvisibleWindow();
	}
}
