package server.mouse.handler;

import common.gui.GuiManager;
import common.main.ErrorListener;
import common.mouse.handler.MouseHandler;
import common.network.NetworkManager;
import server.mouse.listener.ServerMouseListener;

public class ServerMouseHandler extends MouseHandler implements ServerMouseListener {
    public ServerMouseHandler(NetworkManager network, GuiManager gui, ErrorListener errorListener) {
        super(network, gui, errorListener);
    }
}
