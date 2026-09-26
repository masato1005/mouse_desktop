package server.network.handler;

import com.fasterxml.jackson.core.JsonProcessingException;

import common.data.KeyboardData;
import common.data.MouseData;
import common.eventType.WallType;
import common.gui.GuiManager;
import common.json.InputConvertedData;
import common.main.ErrorListener;
import common.network.handler.NetworkHandler;
import server.keyboard.ServerKeyboardManager;
import server.mouse.ServerMouseManager;
import server.network.ServerNetworkManager;
import server.network.listener.ServerNetworkListener;

public class ServerNetworkHandler extends NetworkHandler implements ServerNetworkListener {
    private final ServerNetworkManager network;
    private final ServerMouseManager mouse;
    private final ServerKeyboardManager keyboard;

    public ServerNetworkHandler(ServerNetworkManager network, GuiManager gui, ServerMouseManager mouse,
            ServerKeyboardManager keyboard, ErrorListener errorListener) {
        super(network, gui, mouse, keyboard, errorListener);
        this.network = network;
        this.mouse = mouse;
        this.keyboard = keyboard;
    }

    @Override
    public void receiveData(InputConvertedData data) {
        try {
            switch (data.getDataType()) {
                case MOUSE -> {
                    MouseData mouseData = mapper.treeToValue(data.getData(), MouseData.class);
                    mouse.receiveData(mouseData);
                }
                case WALL_TYPE ->
                    mouse.setWallType(mapper.treeToValue(data.getData(), WallType.class));
                case SYSTEM_EXIT -> System.exit(0);
                case KEYBOARD -> {
                    KeyboardData keyboardData;
                    try {
                        keyboardData = mapper.treeToValue(data.getData(), KeyboardData.class);
                        keyboard.receiveData(keyboardData);
                    } catch (JsonProcessingException | IllegalArgumentException e) {
                        errorListener.happenError("Jsonへの変換でエラーが発生しました");
                    }
                }
                default -> {
                }
            }
        } catch (JsonProcessingException | IllegalArgumentException e) {
            errorListener.happenError("Jsonへの変換でエラーが発生しました");
        }
    }

    @Override
    public void waitingClient() {
        gui.showWaitingServerGui();
    }

    @Override
    public void successConnect() {
        network.startSendThread();
        gui.removeWaitingServer();
        gui.initInvisibleWindow();
        gui.successConnectGui();
        mouse.start();
    }

}
