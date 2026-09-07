package server;

import common.keyboard.KeyboardManager;
import common.gui.GuiManager;
import common.gui.handler.GuiHandler;
import common.main.AppCallback;
import common.main.Controller;
import common.main.ErrorListener;
import common.network.handler.NetworkHandler;
import server.gui.handler.ServerGuiHandler;
import server.mouse.ServerMouseManager;
import server.network.ServerNetworkManager;
import server.network.handler.ServerNetworkHandler;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class ServerController extends Controller {
    private final AppCallback appCallback;
    private final ServerNetworkManager serverNetwork;

    public ServerController(int portNumber, ServerNetworkManager network, GuiManager gui, ServerMouseManager mouse,
            KeyboardManager keyboard, AppCallback appCallback) {
        super(portNumber, network, gui, mouse, keyboard);
        this.serverNetwork = network;
        this.appCallback = appCallback;
    }

    @Override
    protected void startManagers() {
        serverNetwork.start();
	}

    @Override
    protected NetworkHandler createNetworkHandler(ErrorListener errorListener) {
        return new ServerNetworkHandler(serverNetwork, gui, (ServerMouseManager) mouse, keyboard, errorListener);
    }

    @Override
    protected GuiHandler createGuiHandler(ErrorListener errorListener) {
        return new ServerGuiHandler(serverNetwork, gui, mouse, appCallback, errorListener);
    }
}
