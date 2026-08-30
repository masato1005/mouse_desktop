package server;

import com.fasterxml.jackson.core.JsonProcessingException;

import common.EventType.WallType;
import common.Json.InputConvertedData;
import common.Keyboard.KeyboardManager;
import common.Mouse.MouseManager;
import common.data.MouseData;
import common.gui.GuiManager;
import common.gui.contents.ErrorExitGui;
import common.main.AppCallback;
import common.main.Controller;
import server.network.ServerNetworkManager;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class ServerController extends Controller {
    private final AppCallback appCallback;
    private final ServerNetworkManager serverNetwork;

    public ServerController(int portNumber, ServerNetworkManager network, GuiManager gui, MouseManager mouse,
            KeyboardManager keyboard, AppCallback appCallback) {
        super(portNumber, network, gui, mouse, keyboard);
        this.serverNetwork = network;
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
        serverNetwork.stopUdpServer();
        appCallback.returnToAppSelection();
    }
}
