package client;

import com.fasterxml.jackson.core.JsonProcessingException;

import client.network.ClientNetworkManager;
import common.EventType.MouseEventType;
import common.Json.InputConvertedData;
import common.Keyboard.KeyboardManager;
import common.Mouse.MouseManager;
import common.data.MouseData;
import common.gui.GuiManager;
import common.gui.contents.ErrorExitGui;
import common.main.Controller;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class ClientController extends Controller {
    private final ClientNetworkManager clientNetwork;

    public ClientController(int portNumber, ClientNetworkManager network, GuiManager gui, MouseManager mouse,
            KeyboardManager keyboard) {
        super(portNumber, network, gui, mouse, keyboard);
        this.clientNetwork = network;
    }

    @Override
    protected void startNet() {
        clientNetwork.start();
	}

    @Override
    protected void viewChoiceWallTypeGui() {
        gui.viewChoiceWallTypeGui();
    }

    @Override
    protected void successConnect() {
        gui.initInvisibleWindow();
        mouse.start();
        gui.successConnectGui();
    }

    @Override
    protected void receiveData(InputConvertedData data) {
        switch (data.getDataType()) {
            case MOUSE -> {
                MouseData mouseData;
                try {
                    mouseData = mapper.treeToValue(data.getData(), MouseData.class);
                    if (mouseData.getMouseEventType() == MouseEventType.TOUCH_WALL) {
                        mouse.returnMouseToClient(mouseData);
                    }
                } catch (JsonProcessingException | IllegalArgumentException e) {
                    new ErrorExitGui("Jsonへの変換でエラーが発生しました");
                }

            }
            case SYSTEM_EXIT -> System.exit(0);
            default -> {
            }
        }
    }

    @Override
    protected void retryProcess() {
        try {
            clientNetwork.start();
        } catch (Exception e) {
            new ErrorExitGui("再検索が正しく行われませんでした。");
        }

    }

    @Override
    protected void timeoutProcess() {
        gui.showTimeout();
    }
}
