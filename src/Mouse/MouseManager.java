package Mouse;

import EventType.WallType;
import Handler.MouseHandler;
import Json.MouseData;
import Listener.implemented.ImplementedMouseListener;
import Mouse.Cntents.MouseDrawer;
import Mouse.Cntents.MouseScanner;

public class MouseManager {
	private final ImplementedMouseListener listener = new ImplementedMouseListener();

	private MouseScanner mouseScanner = new MouseScanner(this, listener);
	private MouseDrawer mouseDrawer = new MouseDrawer(this);
	private MouseHandler handler;

	public void start() {
		mouseScanner.start();
	}

	public void mouseMove(MouseData mouseData) {
		listener.mouseMoved(mouseData);
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
