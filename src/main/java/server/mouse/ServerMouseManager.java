package server.mouse;

import common.data.MouseData;
import common.eventtype.WallType;
import common.mouse.MouseManager;
import common.mouse.listener.MouseCallback;

public class ServerMouseManager extends MouseManager implements MouseCallback {
    private ServerCursorLocater cursorLocater;

    @Override
    public void start() {
        cursorLocater = new ServerCursorLocater(listener, errorListener, this);
    }

    @Override
    public void setWallType(WallType wallType) {
        cursorLocater.setWallType(wallType);
    }

    @Override
    public void receiveData(MouseData mouseData) {
        cursorLocater.update(mouseData);
    }

    @Override
    public void mouseMoved(MouseData mouseData) {
        robotExecutor.mouseMoved(mouseData);
    }

    @Override
    public void buttonPressed(int buttonNumber) {
        robotExecutor.buttonPressed(buttonNumber);
    }

    @Override
    public void buttonReleased(int buttonNumber) {
        robotExecutor.buttonReleased(buttonNumber);
    }

    @Override
    public void errorHandle() {
    }

    @Override
    public void wheelMoved(int moveAmount) {
        robotExecutor.wheelMove(moveAmount);
    }
}
