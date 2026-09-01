package common.Mouse;

import common.EventType.MouseEventType;
import common.EventType.WallType;
import common.Listener.MouseEventListener;
import common.Listener.implemented.ImplementedMouseListener;
import common.Mouse.Contents.CMouse;
import common.Mouse.Contents.SMouse;
import common.data.Modifiers;
import common.data.MouseData;
import common.main.ErrorHandle;
import common.main.ErrorListener;

public class MouseManager implements ErrorHandle {
	private final ImplementedMouseListener eventListener = new ImplementedMouseListener();

	private SMouse mouseScanner;
	private CMouse mouseDrawer;

	private ErrorListener errorListener;

	public void setEventListener(MouseEventListener eventListener) {
		this.eventListener.setListener(eventListener);
	}

	public void setScannerWallType(WallType type) {
		mouseScanner.setWallType(type);
	}

	public void setDrawerWallType(WallType wallType) {
		mouseDrawer.setWallType(wallType);
	}

	public void start() {
		mouseScanner = new SMouse(this, eventListener, errorListener);
		mouseDrawer = new CMouse(this, eventListener, errorListener);
		mouseScanner.start();
	}

	public void mouseMove(MouseData mouseData) {
		eventListener.mouseMoved(mouseData);
	}

	public void clickMouse(MouseEventType type, boolean pressed) {
		MouseData mouseData = new MouseData(type, 0, 0, 0, 0, 0, pressed, new Modifiers());

		switch (type) {
			case LEFT_CLICK -> eventListener.mouseLeftClicked(mouseData);
			case WHEEL_CLICK -> eventListener.mouseWheelClicked(mouseData);
			case RIGHT_CLICK -> eventListener.mouseRightClicked(mouseData);
			default -> throw new IllegalArgumentException("クリック以外のイベントです: " + type);
		}
	}

	public void moveWheel(int amount) {
		MouseData mouseData = new MouseData(MouseEventType.WHEEL_MOVE, 0, 0, 0, 0, amount, false, new Modifiers());
		eventListener.mouseWheelMoved(mouseData);
	}

	public void updateDrawer(MouseData mouseData) {
		mouseDrawer.update(mouseData);
	}

	public void returnMouse(MouseData mouseData) {
		eventListener.mouseMoved(mouseData);
	}

	public void returnMouseToClient(MouseData mouseData) {
		mouseScanner.returnMouse(mouseData);
	}

	public void openInvisibleWindow() {
		eventListener.openInvisibleWindow();
	}

	public void closeInvisibleWindow() {
		eventListener.closeInvisibleWindow();
	}

	@Override
	public void errorHandle() {
	}

	public void setErrorListener(ErrorListener errorListener) {
		this.errorListener = errorListener;
	}
}
