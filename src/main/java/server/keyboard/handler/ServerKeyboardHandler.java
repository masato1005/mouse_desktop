package server.keyboard.handler;

import common.gui.GuiManager;
import common.keyboard.handler.KeyboardHandler;
import common.main.ErrorListener;
import common.network.NetworkManager;
import server.keyboard.listener.ServerKeyboardListener;

public class ServerKeyboardHandler extends KeyboardHandler implements ServerKeyboardListener{

    public ServerKeyboardHandler(NetworkManager network, GuiManager gui, ErrorListener errorListener) {
        super(network, gui, errorListener);
    }

}
