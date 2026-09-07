package common.mouse.handler;

import com.fasterxml.jackson.core.JsonProcessingException;

import common.data.MouseData;
import common.eventtype.DataType;
import common.gui.GuiManager;
import common.json.JsonConverter;
import common.main.ErrorListener;
import common.mouse.listener.MouseListener;
import common.network.NetworkManager;

public class MouseHandler implements MouseListener {
    private final NetworkManager network;
    private final GuiManager gui;
    private final ErrorListener errorListener;

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
        try {
            String json = JsonConverter.toJson(DataType.MOUSE, mouseData);
            network.sendData(json);
        } catch (JsonProcessingException e) {
            errorListener.happenError("Json処理で不具合が発生しました");
        }
        
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
        
    }

    @Override
    public void mouseTouchWall(MouseData mouseData) {
        gui.closeInvisibleWindow();
        network.touchWall(mouseData);
    }
}
