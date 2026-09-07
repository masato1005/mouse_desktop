package client;

import client.mouse.ClientMouseManager;
import client.network.ClientNetworkManager;
import common.keyboard.KeyboardManager;
import common.gui.GuiManager;
import common.gui.handler.GuiHandler;
import common.main.Controller;
import common.main.ErrorListener;
import common.network.handler.NetworkHandler;
import client.gui.handler.ClientGuiHandler;
import client.network.handler.ClientNetworkHandler;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class ClientController extends Controller {
    private final ClientNetworkManager clientNetwork;

    public ClientController(int portNumber, ClientNetworkManager network, GuiManager gui, ClientMouseManager mouse,
            KeyboardManager keyboard) {
        super(portNumber, network, gui, mouse, keyboard);
        this.clientNetwork = network;
    }

    @Override
    protected void startManagers() {
        clientNetwork.start();
	}

    @Override
    protected NetworkHandler createNetworkHandler(ErrorListener errorListener) {
        return new ClientNetworkHandler(clientNetwork, gui, (ClientMouseManager) mouse, keyboard, errorListener);
    }

    @Override
    protected GuiHandler createGuiHandler(ErrorListener errorListener) {
        return new ClientGuiHandler(clientNetwork, gui, mouse, errorListener);
    }
}
