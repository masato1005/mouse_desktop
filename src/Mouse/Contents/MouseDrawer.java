package Mouse.Contents;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;

import EventType.MouseEventType;
import EventType.WallType;
import Json.MouseData;
import Mouse.MouseManager;

public class MouseDrawer {
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	private final int width = screenSize.width;
	private final int height = screenSize.height;
	private final int wallMoveRange = 3;
	private final int wallMoveCoolTimeRange = 10;

	private int mouseX;
	private int mouseY;
	private boolean haveMouse = false;
	private boolean justGetMouse = false;

	private final MouseManager manager;
	private WallType wallType = WallType.EAST;
	private Robot robot;

	public MouseDrawer(MouseManager manager) {
		this.manager = manager;
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	public void update(MouseData mouseData) {
		if (mouseData.getMouseEventType() == MouseEventType.WHEELMOVE) {
			robot.mouseWheel(mouseData.getWheelAmount());
			return;
		}

		switch (mouseData.getMouseEventType()) {
			case LEFTCLICK -> {
				updateButton(InputEvent.BUTTON1_DOWN_MASK, mouseData.isPressed());
				return;
			}
			case WHEELCLICK -> {
				updateButton(InputEvent.BUTTON2_DOWN_MASK, mouseData.isPressed());
				return;
			}
			case RIGHTCLICK -> {
				updateButton(InputEvent.BUTTON3_DOWN_MASK, mouseData.isPressed());
				return;
			}
			default -> {
			}
		}

		if (haveMouse) {
			mouseX = mouseX + mouseData.getDx();
			mouseY = mouseY + mouseData.getDy();

			if (mouseX > width)
				mouseX = width;
			if (mouseX < 0)
				mouseX = 0;
			if (mouseY > height)
				mouseY = height;
			if (mouseY < 0)
				mouseY = 0;

			if(!checkTouchWall()){
				MouseData sendMouseData = new MouseData();
				sendMouseData.setMouseEventType(MouseEventType.TOUCHWALL);
				sendMouseData.setMouseX(mouseX);
				sendMouseData.setMouseY(mouseY);

				manager.returnMouse(sendMouseData);
				manager.openInvisibleWindow();
				return;
			}
			draw();

		} else {
			setHaveMouse(true);
			manager.closeInvisibleWindow();
			switch (wallType) {
				case NORTH -> {
					mouseX = clamp(mouseData.getMouseX(), 0, width - 1);
					mouseY = 0;
				}
				case SOUTH -> {
					mouseX = clamp(mouseData.getMouseX(), 0, width - 1);
					mouseY = height - 1;
				}
				case WEST -> {
					mouseX = 0;
					mouseY = clamp(mouseData.getMouseY(), 0, height - 1);
				}
				case EAST -> {
					mouseX = width - 1;
					mouseY = clamp(mouseData.getMouseY(), 0, height - 1);
				}
			}
			draw();
		}
	}

	private int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(value, max));
	}

	private void draw() {
		robot.mouseMove(mouseX, mouseY);
	}

	private void updateButton(int buttonMask, boolean pressed) {
		if (pressed) {
			robot.mousePress(buttonMask);
		} else {
			robot.mouseRelease(buttonMask);
		}
	}

	private void setHaveMouse(Boolean haveMouse) {
		this.haveMouse = haveMouse;
	}

	private void setJustGetMouse(boolean justGetMouse) {
		this.justGetMouse = justGetMouse;
	}

	public void setWallType(WallType wallType) {
		if (wallType == WallType.NORTH)
			this.wallType = WallType.SOUTH;
		if (wallType == WallType.SOUTH)
			this.wallType = WallType.NORTH;
		if (wallType == WallType.WEST)
			this.wallType = WallType.EAST;
		if (wallType == WallType.EAST)
			this.wallType = WallType.WEST;
	}

	private boolean checkTouchWall() {
		if (!justGetMouse) {
			switch (wallType) {
				case NORTH -> {
					if (mouseY <= wallMoveRange) {
						setHaveMouse(false);
						setJustGetMouse(true);
					}
				}
				case SOUTH -> {
					if (mouseY >= height - wallMoveRange) {
						setHaveMouse(false);
						setJustGetMouse(true);
					}
				}
				case WEST -> {
					if (mouseX <= wallMoveRange) {
						setHaveMouse(false);
						setJustGetMouse(true);
					}
				}
				case EAST -> {
					if (mouseX >= width - wallMoveRange) {
						setHaveMouse(false);
						setJustGetMouse(true);
					}
				}
			}
		} else {
			switch (wallType) {
				case NORTH -> {
					if (mouseY > wallMoveCoolTimeRange) {
						setJustGetMouse(false);
					}
				}
				case SOUTH -> {
					if (mouseY < height - wallMoveCoolTimeRange) {
						setJustGetMouse(false);
					}
				}
				case WEST -> {
					if (mouseX > wallMoveCoolTimeRange) {
						setJustGetMouse(false);
					}
				}
				case EAST -> {
					if (mouseX < width - wallMoveCoolTimeRange) {
						setJustGetMouse(false);
					}
				}
			}
		}
		return haveMouse;
	}
}
