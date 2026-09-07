package client.gui.handler;

import client.network.ClientNetworkManager;
import common.gui.GuiManager;
import common.gui.handler.GuiHandler;
import common.main.ErrorListener;
import common.mouse.MouseManager;

public class ClientGuiHandler extends GuiHandler {
    private final ClientNetworkManager clientNetwork;

    public ClientGuiHandler(ClientNetworkManager network, GuiManager gui, MouseManager mouse,
            ErrorListener errorListener) {
        super(network, gui, mouse, errorListener);
        this.clientNetwork = network;
    }

    @Override
    public void retry() {
        try {
            clientNetwork.start();
        } catch (Exception e) {
            errorListener.happenError("再検索が正しく行われませんでした。");
        }
    }
}
