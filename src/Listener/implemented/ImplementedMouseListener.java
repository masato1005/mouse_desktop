package Listener.implemented;

import EventType.DataType;
import Handler.MouseHandler;
import Json.JsonConverter;
import Json.MouseData;
import Listener.MouseListener;

public class ImplementedMouseListener implements MouseListener {
    private MouseHandler handler;

    public void setListener(MouseHandler mouseHandler) {
        this.handler = mouseHandler;
    }

    @Override
    public void mouseLeftClicked(MouseData mouseData) {
        sendMouseData(mouseData);
    }

    @Override
    public void mouseMoved(MouseData mouseData) {
        sendMouseData(mouseData);
    }

    @Override
    public void mouseRightClicked(MouseData mouseData) {
        sendMouseData(mouseData);
    }

    @Override
    public void mouseDragged() {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public void mouseWheelClicked(MouseData mouseData) {
        sendMouseData(mouseData);
    }

    @Override
    public void mouseWheelMoved(MouseData mouseData) {
        sendMouseData(mouseData);
    }

    private void sendMouseData(MouseData mouseData) {
        handler.mouseMoved(
                new JsonConverter().dataConverter(
                        DataType.MOUSE, mouseData));
    }

    @Override
    public void openInvisibleWindow() {
        handler.openInvisibleWindow();
    }

    @Override
    public void closeInvisibleWindow() {
        handler.closeInvisibleWindow();
    }
}
