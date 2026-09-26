package common.robot;

import common.data.KeyboardData;

public interface KeyboardRobotExecutor {
    public void pressedKey(KeyboardData keyboardData);
    public void ReleasedKey(KeyboardData keyboardData);
}
