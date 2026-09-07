package common.robot;

import common.data.MouseData;

public interface MouseRobotExecutor {

    public void mouseMoved(MouseData mouseData);

    public void buttonPressed(int buttonNumber);

    public void buttonReleased(int buttonNumber);

    public void wheelMove(int moveAmount);
}
