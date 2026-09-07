package client.network.handler;

import com.fasterxml.jackson.core.JsonProcessingException;

import client.mouse.ClientMouseManager;
import client.network.ClientNetworkManager;
import client.network.listener.ClientNetworkListener;
import common.eventtype.MouseEventType;
import common.json.InputConvertedData;
import common.keyboard.KeyboardManager;
import common.data.MouseData;
import common.gui.GuiManager;
import common.main.ErrorListener;
import common.network.handler.NetworkHandler;

public class ClientNetworkHandler extends NetworkHandler implements ClientNetworkListener {

    private final ClientNetworkManager network;
    private final ClientMouseManager mouse;

    public ClientNetworkHandler(ClientNetworkManager network, GuiManager gui, ClientMouseManager mouse,
            KeyboardManager keyboard, ErrorListener errorListener) {
        super(network, gui, mouse, keyboard, errorListener);
        this.network = network;
        this.mouse = mouse;
    }

    @Override
    public void receiveData(InputConvertedData data) {
        switch (data.getDataType()) {
            case MOUSE -> {
                MouseData mouseData;
                try {
                    mouseData = mapper.treeToValue(data.getData(), MouseData.class);
                    if (mouseData.getMouseEventType() == MouseEventType.TOUCH_WALL) {
                        mouse.receiveData(mouseData);
                    }
                } catch (JsonProcessingException | IllegalArgumentException e) {
                    errorListener.happenError("Jsonへの変換でエラーが発生しました");
                }

            }
            case SYSTEM_EXIT -> System.exit(0);
            default -> {
            }
        }
    }

    @Override
    public void timeout() {
        gui.showTimeout();
    }

    @Override
    public void checkNofitication() {
        gui.showTimeout();
    }

    @Override
    public void successConnect() {
        gui.initInvisibleWindow();
        mouse.start();
        gui.successConnectGui();
    }
}
