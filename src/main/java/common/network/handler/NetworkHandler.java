package common.network.handler;

import com.fasterxml.jackson.databind.ObjectMapper;

import common.json.InputConvertedData;
import common.keyboard.KeyboardManager;
import common.gui.GuiManager;
import common.mouse.MouseManager;
import common.main.ErrorListener;
import common.network.NetworkManager;
import common.network.listener.NetworkListener;

public abstract class NetworkHandler implements NetworkListener {
    private final NetworkManager network;
    protected  final GuiManager gui;
    private final MouseManager mouse;
    private final KeyboardManager keyboard;
    protected final ErrorListener errorListener;

    protected final ObjectMapper mapper = new ObjectMapper();
    protected boolean updating = true;


    public NetworkHandler(NetworkManager network, GuiManager gui, MouseManager mouse, KeyboardManager keyboard,
            ErrorListener errorListener) {
        this.network = network;
        this.gui = gui;
        this.mouse = mouse;
        this.keyboard = keyboard;
        this.errorListener = errorListener;
    }

    
    @Override
    public void receiveError() {
        updating = false;
    }

    @Override
    public abstract void receiveData(InputConvertedData data);

    @Override
    public abstract void successConnect();

}
