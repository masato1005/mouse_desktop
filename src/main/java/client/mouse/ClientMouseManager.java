package client.mouse;

import common.EventType.WallType;
import common.Listener.MouseEventListener;
import common.Listener.implemented.ImplementedMouseListener;
import common.data.MouseData;
import common.main.ErrorHandle;
import common.main.ErrorListener;
import server.mouse.MouseCallback;

public class ClientMouseManager implements ErrorHandle, MouseCallback{
    private final ImplementedMouseListener eventListener = new ImplementedMouseListener();
    
    private ClientCursorLocater clientMouse;
    private ErrorListener errorListener;

    public void setEventListener(MouseEventListener eventListener) {
        this.eventListener.setListener(eventListener);
    }

    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public void setWallType(WallType wallType) {
        clientMouse.setWallType(wallType);
    }

    public void start() {
        clientMouse = new ClientCursorLocater(eventListener, errorListener,this);
    }

    public void receiveData(MouseData mouseData){
        clientMouse.update(mouseData);
    }

    @Override
    public void errorHandle() {
        
    }

    @Override
    public void receivedCursor() {
        
    }

    @Override
    public void touchWall(MouseData mouseData){
        
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


}
