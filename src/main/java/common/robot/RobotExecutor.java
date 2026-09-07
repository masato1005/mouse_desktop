package common.robot;

import java.awt.AWTException;
import java.awt.Robot;

import common.data.MouseData;
import common.main.ErrorListener;

public class RobotExecutor implements MouseRobotExecutor {
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

    public boolean isRobotNull(){
        if(robot == null)
            return true;
        return false;
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
}
