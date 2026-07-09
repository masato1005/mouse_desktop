package Mouse.Contents;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;

import EventType.MouseEventType;
import EventType.WallType;
import Json.Modifiers;
import Json.MouseData;
import Listener.MouseListener;
import Mouse.MouseManager;

public class MouseScanner {
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private final int starttime = 0;
	private final int rate = 16;
	private final int wallMoveRange = 3;
	private final int wallMoveCoolTimeRange = 10;
	private final int width = screenSize.width;
	private final int height = screenSize.height;

	private int mouseX = MouseInfo.getPointerInfo().getLocation().x;
	private int mouseY = MouseInfo.getPointerInfo().getLocation().y;
	private int preMouseX;
	private int preMouseY;
	private int dx;
	private int dy;
	private boolean haveMouse = true;
	private boolean justGetMouse = false;

	private WallType wallType = WallType.WEST;

	private final MouseManager manager;
	private final MouseListener listener;
	private Robot robot;

	public MouseScanner(MouseManager manager, MouseListener listener) {
		this.manager = manager;
		this.listener = listener;
		try {
			robot = new Robot();
		} catch (AWTException ex) {
			System.getLogger(MouseScanner.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
		}
	}

	private void setJustGetMouse(boolean justGetMouse) {
		this.justGetMouse = justGetMouse;
	}

	private int getMovementX() {
		return dx = mouseX - preMouseX;
	}

	private int getMovementY() {
		return dy = mouseY - preMouseY;
	}

	private int getMouseX() {
		return mouseX;
	}

	private void setMouseX(int mouseX) {
		this.mouseX = mouseX;
	}

	private int getMouseY() {
		return mouseY;
	}

	private void setMouseY(int mouseY) {
		this.mouseY = mouseY;
	}

	private void setMouseCenter() {
		int centerX = width / 2;
		int centerY = height / 2;
		robot.mouseMove(centerX, centerY);
		mouseX = centerX;
		mouseY = centerY;
		preMouseX = mouseX;
		preMouseY = mouseY;
	}

	private void nowMousePosition() {
		preMouseX = mouseX;
		preMouseY = mouseY;
		this.mouseX = MouseInfo.getPointerInfo().getLocation().x;
		this.mouseY = MouseInfo.getPointerInfo().getLocation().y;
	}

	public void start() {
		Timer timer = new Timer(true);
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				nowMousePosition();
				getMovementX();
				getMovementY();
				if (haveMouse) {
					checkTouchWall();
				}

				if (!haveMouse) {
					MouseData mouseData = new MouseData(
							MouseEventType.MOVE,
							mouseX,
							mouseY,
							dx,
							dy,
							0,
							false,
							new Modifiers());
					manager.mouseMove(mouseData);
					setMouseCenter();
				}

			}
		};
		timer.scheduleAtFixedRate(task, starttime, rate);
	}

	private void checkTouchWall() {
		if (!justGetMouse) {
			switch (wallType) {
				case NORTH -> {
					if (mouseY <= wallMoveRange) {
						setHaveMouse(false);
						listener.openInvisibleWindow();
					}
				}
				case SOUTH -> {
					if (mouseY >= height - wallMoveRange) {
						setHaveMouse(false);
						listener.openInvisibleWindow();
					}
				}
				case WEST -> {
					if (mouseX <= wallMoveRange) {
						setHaveMouse(false);
						listener.openInvisibleWindow();
					}
				}
				case EAST -> {
					if (mouseX >= width - wallMoveRange) {
						setHaveMouse(false);
						listener.openInvisibleWindow();
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
	}

	private void setHaveMouse(boolean haveMouse) {
		this.haveMouse = haveMouse;
	}

	public void setWallType(WallType type) {
		this.wallType = type;
	}

	public void returnMouse(MouseData mouseData) {
		if (wallType == WallType.EAST || wallType == WallType.WEST) {
			mouseX = width - mouseData.getMouseX();
			mouseY = mouseData.getMouseY();
		} else {
			mouseX = mouseData.getMouseX();
			mouseY = height - mouseData.getMouseY();
		}

		robot.mouseMove(mouseX, mouseY);

		this.preMouseX = mouseX;
		this.preMouseY = mouseY;
		setHaveMouse(true);
		setJustGetMouse(true);
		listener.closeInvisibleWindow();
	}


}
