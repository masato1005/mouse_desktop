package server.mouse;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.InputEvent;

import common.data.MouseData;
import static common.eventtype.MouseEventType.LEFT_CLICK;
import static common.eventtype.MouseEventType.MOVE;
import static common.eventtype.MouseEventType.RIGHT_CLICK;
import static common.eventtype.MouseEventType.TOUCH_WALL;
import static common.eventtype.MouseEventType.WHEEL_CLICK;
import common.eventtype.WallType;
import common.main.ErrorListener;
import common.mouse.listener.MouseCallback;
import common.mouse.listener.MouseListener;

public class ServerCursorLocater {
    private final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    private final long RETURN_MOVE_WAIT_NANOS = 16_000_000L;
    private final int SCREEN_WIDTH = SCREEN_SIZE.width;
    private final int SCREEN_HEIGHT = SCREEN_SIZE.height;
    private final int WALL_RANGE = 3;
    private final int WALL_COOL_TIME_RANGE = 10;

    private final MouseListener listener;
    private final ErrorListener errorListener;
    private final MouseCallback mouseCallback;

    private int mouseX;
    private int mouseY;
    private boolean haveMouse = false;
    private boolean justGetMouse = false;

    private long sendTime = 0L;

    private WallType wallType = WallType.WEST;

    public ServerCursorLocater(MouseListener listener, ErrorListener errorListener, MouseCallback mouseCallback) {
        this.listener = listener;
        this.errorListener = errorListener;
        this.mouseCallback = mouseCallback;
    }

    public void setWallType(WallType wallType) {
        this.wallType = wallType.getOppositeWall();
    }

    public void update(MouseData mouseData) {
        if (mouseData.getMouseEventType() == MOVE
                && !haveMouse
                && checkCoolTime()) {
            return;
        }

        switch (mouseData.getMouseEventType()) {
            case MOVE -> moveMouseProcess(mouseData);
            case LEFT_CLICK -> updateButton(InputEvent.BUTTON1_DOWN_MASK, mouseData.isPressed());
            case WHEEL_CLICK -> updateButton(InputEvent.BUTTON2_DOWN_MASK, mouseData.isPressed());
            case RIGHT_CLICK -> updateButton(InputEvent.BUTTON3_DOWN_MASK, mouseData.isPressed());
            case CLOSE_INVISIBLE_WINDOW -> {
            }
            case DRAG -> {
            }
            case SEND_MOUSE -> {
            }
            case TOUCH_WALL -> {
                // No processing is required.
            }
            case WHEEL_MOVE -> moveWheelProcess(mouseData);
            default -> {
            }
        }
    }

    private void moveWheelProcess(MouseData mouseData) {
        mouseCallback.wheelMoved(mouseData.getWheelAmount());
    }

    public boolean checkCoolTime() {
        return System.nanoTime() < sendTime + RETURN_MOVE_WAIT_NANOS;
    }

    private void moveMouseProcess(MouseData mouseData) {
        if (haveMouse) {
            haveMouseProcess(mouseData);
        } else {
            notHaveMouseProcess(mouseData);
        }
    }

    private void haveMouseProcess(MouseData mouseData) {
        updateCursorLocate(mouseData);
        boolean touchWall = checkTouchWall();
        if (touchWall) {
            haveMouse = false;
            listener.openInvisibleWindow();
            MouseData sendMouse = new MouseData(mouseX, mouseY);
            sendMouse.setMouseEventType(TOUCH_WALL);
            listener.mouseTouchWall(sendMouse);
            sendTime = System.nanoTime();
            return;
        }
        draw();
        checkEscapeCoolTimeArea();
    }

    private void updateCursorLocate(MouseData mouseData) {
        mouseX += mouseData.getDx();
        mouseY += mouseData.getDy();
        checkOutWall();
    }

    private void checkOutWall() {
        if (mouseX >= SCREEN_WIDTH) {
            mouseX = SCREEN_WIDTH - 1;
        }
        if (mouseX <= 0) {
            mouseX = 0;
        }
        if (mouseY >= SCREEN_HEIGHT) {
            mouseY = SCREEN_HEIGHT - 1;
        }
        if (mouseY <= 0) {
            mouseY = 0;
        }
    }

    private boolean checkTouchWall() {
        if (justGetMouse) {
            return false;
        }
        MouseData mouseData = new MouseData(mouseX, mouseY);
        return wallType.isTouchWall(mouseData, WALL_RANGE, SCREEN_SIZE);
    }

    private void draw() {
        mouseCallback.mouseMoved(new MouseData(mouseX, mouseY));
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

    private void notHaveMouseProcess(MouseData mouseData) {
        haveMouse = true;
        justGetMouse = true;
        listener.closeInvisibleWindow();
        setLocate(mouseData);
        draw();
    }

    private void setLocate(MouseData mouseData) {
        MouseData mouseXAndMouseY = wallType.getLocate(mouseData, SCREEN_SIZE);
        mouseX = mouseXAndMouseY.getMouseX();
        mouseY = mouseXAndMouseY.getMouseY();
    }

    private void updateButton(int buttonNumber, boolean pressed) {
        if (pressed) {
            mouseCallback.buttonPressed(buttonNumber);
        } else {
            mouseCallback.buttonReleased(buttonNumber);
        }
    }
}
