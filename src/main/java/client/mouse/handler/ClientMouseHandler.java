package client.mouse.handler;

import client.keyboard.ClientKeyboardManager;
import client.mouse.listener.ClientMouseListener;
import common.data.MouseData;
import common.gui.GuiManager;
import common.main.ErrorListener;
import common.mouse.handler.MouseHandler;
import common.network.NetworkManager;

public class ClientMouseHandler extends MouseHandler implements ClientMouseListener{
    private final ClientKeyboardManager keyboard;

    public ClientMouseHandler(NetworkManager network, GuiManager gui, ClientKeyboardManager keyboard, ErrorListener errorListener) {
        super(network, gui, errorListener);
        this.keyboard = keyboard;
    }

    @Override
    public void mouseTouchWall(MouseData mouseData) {
        gui.openInvisibleWindow();
        network.touchWall(mouseData);
        keyboard.changeCursorOwner(false);
    }
}
