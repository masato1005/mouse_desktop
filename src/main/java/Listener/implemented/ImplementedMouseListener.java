package Listener.implemented;

import Event.OriginalMouseEvent;
import EventType.DataType;
import EventType.MouseEventType;
import Json.JsonConverter;
import Listener.MouseEventListener;
import Listener.MouseListener;
import data.MouseData;

public class ImplementedMouseListener implements MouseListener {
    private MouseEventListener eventListener;

    public void setListener(MouseEventListener eventListener) {
        this.eventListener = eventListener;
    }

    @Override
    public void mouseMoved(MouseData mouseData) {
        sendMouseData(mouseData);
    }

    private void sendMouseData(MouseData mouseData) {
        String json = new JsonConverter().dataConverter(DataType.MOUSE, mouseData);
        eventListener.onMouseEvent(new OriginalMouseEvent(MouseEventType.MOVE, json));
    }

    @Override
    public void mouseLeftClicked(MouseData mouseData) {
        sendMouseData(mouseData);
    }

    @Override
    public void mouseRightClicked(MouseData mouseData) {
        sendMouseData(mouseData);
    }

    @Override
    public void mouseWheelClicked(MouseData mouseData) {
        sendMouseData(mouseData);
    }

    @Override
    public void mouseWheelMoved(MouseData mouseData) {
        sendMouseData(mouseData);
    }

    @Override
    public void mouseDragged() {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public void openInvisibleWindow() {
        eventListener.onMouseEvent(new OriginalMouseEvent(MouseEventType.SEND_MOUSE, null));
    }

    @Override
    public void closeInvisibleWindow() {
        eventListener.onMouseEvent(new OriginalMouseEvent(MouseEventType.CLOSE_INVISIBLE_WINDOW, null));
    }
}
