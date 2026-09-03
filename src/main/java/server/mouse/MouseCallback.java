package server.mouse;

import common.data.MouseData;

public interface MouseCallback {
    public void receivedCursor();
    public void touchWall(MouseData mouseData);
    public void mouseMoved(MouseData mouseData);
    public void buttonPressed(int buttonNumber);
    public void buttonReleased(int buttonNumber);
}
