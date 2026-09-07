package common.mouse;

import common.eventtype.MouseEventType;
import common.eventtype.WallType;
import common.data.Modifiers;
import common.data.MouseData;
import common.main.ErrorHandle;
import common.main.ErrorListener;
import common.mouse.listener.MouseListener;

public abstract class MouseManager implements ErrorHandle {
	protected MouseListener listener;

	protected ErrorListener errorListener;

	public void setEventListener(MouseListener listener) {
		this.listener = listener;
	}

	public void setErrorListener(ErrorListener errorListener) {
		this.errorListener = errorListener;
	}

	public abstract void start();

	public abstract void receiveData(MouseData mouseData);

	public abstract void setWallType(WallType wallType);

	public void mouseMove(MouseData mouseData) {
		listener.mouseMoved(mouseData);
	}

	public void clickMouse(MouseEventType type, boolean pressed) {
		MouseData mouseData = new MouseData(type, 0, 0, 0, 0, 0, pressed, new Modifiers());
		switch (type) {
			case LEFT_CLICK -> listener.mouseLeftClicked(mouseData);
			case WHEEL_CLICK -> listener.mouseWheelClicked(mouseData);
			case RIGHT_CLICK -> listener.mouseRightClicked(mouseData);
			default -> throw new IllegalArgumentException("クリック以外のイベントです: " + type);
		}
	}

	public void moveWheel(int amount) {
		MouseData mouseData = new MouseData(MouseEventType.WHEEL_MOVE, 0, 0, 0, 0, amount, false, new Modifiers());
		listener.mouseWheelMoved(mouseData);
	}

	public void returnMouse(MouseData mouseData) {
		listener.mouseMoved(mouseData);
	}

	public void openInvisibleWindow() {
		listener.openInvisibleWindow();
	}

	public void closeInvisibleWindow() {
		listener.closeInvisibleWindow();
	}

	@Override
	public abstract void errorHandle();
}
