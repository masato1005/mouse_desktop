package common.mouse.listener;

import common.data.MouseData;

public interface MouseCallback {
    public void mouseMoved(MouseData mouseData);
    public void buttonPressed(int buttonNumber);
    public void buttonReleased(int buttonNumber);
    public void wheelMoved(int moveAmount);
}
