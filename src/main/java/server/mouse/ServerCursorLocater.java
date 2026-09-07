package server.mouse;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Toolkit;

import common.data.Modifiers;
import common.data.MouseData;
import common.eventtype.MouseEventType;
import common.eventtype.WallType;
import common.main.ErrorListener;
import common.mouse.listener.MouseCallback;
import common.mouse.listener.MouseListener;

public class ServerCursorLocater {
    private final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    private final MouseListener listener;
    private final ErrorListener errorListener;
    private final MouseCallback mouseCallback;

    private final int WALL_RANGE = 3;
    private final int WALL_COOL_TIME_RANGE = 10;
    private final int SCREEN_WIDTH = SCREEN_SIZE.width;
    private final int SCREEN_HEIGHT = SCREEN_SIZE.height;

    private int mouseX = MouseInfo.getPointerInfo().getLocation().x;
    private int mouseY = MouseInfo.getPointerInfo().getLocation().y;
    private int preMouseX;
    private int preMouseY;
    private int dx;
    private int dy;
    private boolean haveMouse = true;
    private boolean justGetMouse = false;
    private WallType wallType = WallType.WEST;

    public ServerCursorLocater(MouseListener listener, ErrorListener errorListener, MouseCallback mouseCallback) {
        this.listener = listener;
        this.errorListener = errorListener;
        this.mouseCallback = mouseCallback;
    }

    public void setWallType(WallType wallType) {
        this.wallType = wallType;
    }

    public void updateAndCheck() {
        updateCursorLocate();
        updateMovement();

        if (!haveMouse) {
            mouseCallback.mouseMoved(new MouseData(mouseX, mouseY));
            return;
        }

        boolean touchWall = checkTouchWall();
        if (touchWall) {
            MouseData sendMouse = new MouseData(MouseEventType.TOUCH_WALL, mouseX, mouseY, dx, dy, 0, false, new Modifiers());
            listener.mouseTouchWall(sendMouse);
            return;
        }

        checkEscapeCoolTimeArea();
    }

    private void updateCursorLocate() {
        preMouseX = mouseX;
        preMouseY = mouseY;
        this.mouseX = MouseInfo.getPointerInfo().getLocation().x;
        this.mouseY = MouseInfo.getPointerInfo().getLocation().y;
    }

    private void updateMovement() {
        dx = mouseX - preMouseX;
        dy = mouseY - preMouseY;
    }

    private boolean checkTouchWall() {
        if (justGetMouse)
            return false;
        MouseData mouseData = new MouseData(mouseX, mouseY);
        boolean touchWall = wallType.isTouchWall(mouseData, WALL_RANGE, SCREEN_SIZE);
        if (touchWall) {
            haveMouse = false;
            listener.openInvisibleWindow();
            return true;
        }
        return false;
    }

    private boolean checkEscapeCoolTimeArea() {
        MouseData mouseData = new MouseData(mouseX, mouseY);
        boolean escape = wallType.checkEscapeCoolTimeArea(mouseData, WALL_COOL_TIME_RANGE, SCREEN_SIZE);
        if (escape) {
            justGetMouse = false;
            return true;
        }
        return false;
    }

    public void receiveCursor(MouseData mouseData) {
        MouseData receiveMouse = wallType.getLocate(mouseData, SCREEN_SIZE);
        mouseX = receiveMouse.getMouseX();
        mouseY = receiveMouse.getMouseY();

        mouseCallback.mouseMoved(receiveMouse);

        this.preMouseX = mouseX;
        this.preMouseY = mouseY;

        haveMouse = true;
        justGetMouse = true;

        listener.closeInvisibleWindow();
    }

}
