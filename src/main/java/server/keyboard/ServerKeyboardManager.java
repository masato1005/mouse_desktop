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
        
    }

    @Override
    public void start() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void errorHandle() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
