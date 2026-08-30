package main;

import com.fasterxml.jackson.core.JsonProcessingException;

import EventType.WallType;
import Json.InputConvertedData;
import Keyboard.KeyboardManager;
import Mouse.MouseManager;
import data.MouseData;
import gui.GuiManager;
import gui.contents.ErrorExitGui;
import network.ServerManager;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class ServerController extends Controller {
    private final AppCallback appCallback;
    private final ServerManager server;

    public ServerController(int portNumber, ServerManager network, GuiManager gui, MouseManager mouse,
            KeyboardManager keyboard, AppCallback appCallback) {
        super(portNumber, network, gui, mouse, keyboard);
        server = network;
        this.appCallback = appCallback;
    }

    @Override
    protected void successConnect() {
        gui.removeWaitingServer();
        gui.initInvisibleWindow();
        gui.openInvisibleWindow();
        gui.successConnectGui();
    }

    @Override
    protected void receiveData(InputConvertedData data) {
        try {
            switch (data.getDataType()) {
                case MOUSE -> mouse.updateDrawer(mapper.treeToValue(data.getData(), MouseData.class));
                case WALL_TYPE ->
                    mouse.setDrawerWallType(mapper.treeToValue(data.getData(), WallType.class));
                case SYSTEM_EXIT -> System.exit(0);
                default -> {
                }
            }
        } catch (JsonProcessingException | IllegalArgumentException e) {
            new ErrorExitGui("Jsonへの変換でエラーが発生しました");
        }
    }

    @Override
    protected void stopUdpServer() {
        server.stopUdpServer();
        appCallback.returnToAppSelection();
    }
}
