package common.robot;

import java.awt.AWTException;
import java.awt.Robot;

import common.data.KeyboardData;
import common.data.MouseData;
import common.main.ErrorListener;

public class RobotExecutor implements MouseRobotExecutor, KeyboardRobotExecutor {
    private final ErrorListener errorListener;
    private Robot robot;

    public RobotExecutor(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public void start() {
        makeRobot();
    }

    private void makeRobot() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            errorListener.happenError("Robotクラスの生成に失敗しました");
        }
    }

    public boolean isRobotNull() {
        return robot == null;
    }

    @Override
    public void mouseMoved(MouseData mouseData) {
        robot.mouseMove(mouseData.getMouseX(), mouseData.getMouseY());
    }

    @Override
    public void buttonPressed(int buttonNumber) {
        robot.mousePress(buttonNumber);
    }

    @Override
    public void buttonReleased(int buttonNumber) {
        robot.mouseRelease(buttonNumber);
    }

    @Override
    public void wheelMove(int moveAmount) {
        robot.mouseWheel(moveAmount);
    }

    @Override
    public void pressedKey(KeyboardData keyboardData) {
        robot.keyPress(keyboardData.getVirtualKeyCode());
    }

    @Override
    public void ReleasedKey(KeyboardData keyboardData) {
        robot.keyRelease(keyboardData.getVirtualKeyCode());
    }
}
