package client.mouse;

import common.data.MouseData;
import common.eventtype.WallType;
import common.mouse.MouseManager;
import common.mouse.listener.MouseCallback;

public class ClientMouseManager extends MouseManager implements MouseCallback{
    private ClientCursorLocater clientMouse;

    @Override
    public void start() {
        clientMouse = new ClientCursorLocater(listener, errorListener,this);
    }

    @Override
    public void setWallType(WallType wallType) {
        clientMouse.setWallType(wallType);
    }

    @Override
    public void receiveData(MouseData mouseData){
        clientMouse.update(mouseData);
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
