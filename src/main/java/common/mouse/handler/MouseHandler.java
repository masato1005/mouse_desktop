package common.mouse.handler;

import common.data.MouseData;
import common.eventtype.DataType;
import common.gui.GuiManager;
import common.main.ErrorListener;
import common.mouse.listener.MouseListener;
import common.network.NetworkManager;

public class MouseHandler implements MouseListener {
    protected final NetworkManager network;
    protected final GuiManager gui;
    protected final ErrorListener errorListener;

    public MouseHandler(NetworkManager network, GuiManager gui, ErrorListener errorListener) {
        this.network = network;
        this.gui = gui;
        this.errorListener = errorListener;
    }

    @Override
    public void mouseMoved(MouseData mouseData) {
        sendMouseData(mouseData);
    }

    private void sendMouseData(MouseData mouseData) {
        network.addSendQueue(DataType.MOUSE, mouseData);
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
    public void openInvisibleWindow() {
        gui.openInvisibleWindow();
    }

    @Override
    public void closeInvisibleWindow() {
        gui.closeInvisibleWindow();
    }

    @Override
    public void mouseTouchWall(MouseData mouseData) {
        gui.openInvisibleWindow();
        network.touchWall(mouseData);
    }
}
