package client.keyboard.handler;

import client.keyboard.ClientKeyboardManager;
import client.keyboard.listener.ClientKeyboardListener;
import client.network.ClientNetworkManager;
import common.eventtype.DataType;
import common.gui.GuiManager;
import common.keyboard.handler.KeyboardHandler;
import common.main.ErrorListener;

public class ClientKeyboardHandler extends KeyboardHandler implements ClientKeyboardListener{
    private final ClientNetworkManager clientNetwork;

    public ClientKeyboardHandler(ClientNetworkManager network, GuiManager gui, ClientKeyboardManager keyboard, ErrorListener errorListener) {
        super(network, gui, errorListener);
        this.clientNetwork = network;
    }

    @Override
    public void happenKeyboardEvent(DataType dataType, Object data) {
        clientNetwork.addSendQueue(dataType, data);
    }

}
