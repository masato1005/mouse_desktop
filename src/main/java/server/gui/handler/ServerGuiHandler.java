package server.gui.handler;

import common.gui.GuiManager;
import common.gui.handler.GuiHandler;
import common.main.AppCallback;
import common.main.ErrorListener;
import common.mouse.MouseManager;
import server.network.ServerNetworkManager;

public class ServerGuiHandler extends GuiHandler {
    private final ServerNetworkManager serverNetwork;
    private final AppCallback appCallback;

    public ServerGuiHandler(ServerNetworkManager network, GuiManager gui, MouseManager mouse,
            AppCallback appCallback, ErrorListener errorListener) {
        super(network, gui, mouse, errorListener);
        this.serverNetwork = network;
        this.appCallback = appCallback;
    }

    @Override
    public void pushStop() {
        serverNetwork.stopUdpServer();
        appCallback.returnToAppSelection();
    }

    @Override
    public void pushedSuccessOkButton() {
        // The client selects the shared screen wall.
    }
}
