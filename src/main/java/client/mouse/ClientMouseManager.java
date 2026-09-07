package client.mouse;

import common.data.MouseData;
import common.eventtype.WallType;
import common.mouse.MouseManager;
import server.mouse.MouseCallback;

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
        
    }

    @Override
    public void buttonPressed(int buttonNumber) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void buttonReleased(int buttonNumber) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void errorHandle() {

    }


}
