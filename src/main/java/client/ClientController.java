package client;

import client.gui.handler.ClientGuiHandler;
import client.keyboard.ClientKeyboardManager;
import client.mouse.ClientMouseManager;
import client.mouse.handler.ClientMouseHandler;
import client.network.ClientNetworkManager;
import client.network.handler.ClientNetworkHandler;
import common.gui.GuiManager;
import common.gui.handler.GuiHandler;
import common.main.Controller;
import common.main.ErrorListener;
import common.mouse.handler.MouseHandler;
import common.network.handler.NetworkHandler;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class ClientController extends Controller {
    private final ClientNetworkManager clientNetwork;
    private final ClientKeyboardManager clientKeyboard;
    private final ClientMouseManager clientMouse;

    public ClientController(int portNumber, ClientNetworkManager network, GuiManager gui, ClientMouseManager mouse,
            ClientKeyboardManager keyboard) {
        super(portNumber, network, gui, mouse, keyboard);
        this.clientNetwork = network;
        this.clientKeyboard = keyboard;
        this.clientMouse = mouse;
    }

    @Override
    protected void startManagers() {
        clientNetwork.start();
	}

    @Override
    protected NetworkHandler createNetworkHandler(ErrorListener errorListener) {
        return new ClientNetworkHandler(clientNetwork, gui, (ClientMouseManager) mouse, clientKeyboard, errorListener);
    }

    @Override
    protected GuiHandler createGuiHandler(ErrorListener errorListener) {
        return new ClientGuiHandler(clientNetwork, gui, mouse, errorListener);
    }

    @Override
    protected MouseHandler createMouseHandler(ErrorListener errorListener) {
        return new ClientMouseHandler(clientNetwork, gui, clientKeyboard, errorListener);
    }
}
