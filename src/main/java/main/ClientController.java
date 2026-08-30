package main;

import com.fasterxml.jackson.core.JsonProcessingException;

import EventType.MouseEventType;
import Json.InputConvertedData;
import Keyboard.KeyboardManager;
import Mouse.MouseManager;
import data.MouseData;
import gui.GuiManager;
import gui.contents.ErrorExitGui;
import network.ClientManager;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class ClientController extends Controller {
    private final ClientManager client;
    
    public ClientController(int portNumber, ClientManager network, GuiManager gui, MouseManager mouse,
            KeyboardManager keyboard) {
        super(portNumber, network, gui, mouse, keyboard);
        this.client = network;
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

}
