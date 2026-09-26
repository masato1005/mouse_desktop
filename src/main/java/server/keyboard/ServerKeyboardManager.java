package server.keyboard;

import common.data.KeyboardData;
import common.keyboard.KeyboardManager;
import common.robot.KeyboardRobotExecutor;
import server.keyboard.listener.ServerKeyboardListener;

public class ServerKeyboardManager extends KeyboardManager {
    private ServerKeyboardListener serverListener;
    private KeyboardRobotExecutor robot;

    public void setListener(ServerKeyboardListener listener) {
        this.listener = listener;
    }

    public void setRobot(KeyboardRobotExecutor robot) {
        this.robot = robot;
    }

    public void receiveData(KeyboardData data) {
        switch (data.getEventType()) {
            case KEY_DOWN -> robot.pressedKey(data);
            case KEY_UP -> robot.ReleasedKey(data);
            case RELEASE_ALL -> {
            }
        }
    }

    @Override
    public void start() {
        
    }

    @Override
    public void close() {
        
    }

    @Override
    public void errorHandle() {
        
    }

}
