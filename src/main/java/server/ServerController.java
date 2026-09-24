package server;

import common.gui.GuiManager;
import common.gui.handler.GuiHandler;
import common.keyboard.handler.KeyboardHandler;
import common.main.AppCallback;
import common.main.Controller;
import common.main.ErrorListener;
import common.mouse.handler.MouseHandler;
import common.network.handler.NetworkHandler;
import common.robot.KeyboardRobotExecutor;
import server.gui.handler.ServerGuiHandler;
import server.keyboard.ServerKeyboardManager;
import server.keyboard.handler.ServerKeyboardHandler;
import server.mouse.ServerMouseManager;
import server.mouse.handler.ServerMouseHandler;
import server.network.ServerNetworkManager;
import server.network.handler.ServerNetworkHandler;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class ServerController extends Controller {
    private final AppCallback appCallback;
    private final ServerNetworkManager serverNetwork;
    private final ServerMouseManager serverMouse;
    private final ServerKeyboardManager severKeyboard;

    public ServerController(int portNumber, ServerNetworkManager network, GuiManager gui, ServerMouseManager mouse,
            ServerKeyboardManager keyboard, AppCallback appCallback) {
        super(portNumber, network, gui, mouse, keyboard);
        this.serverNetwork = network;
        this.serverMouse = mouse;
        this.appCallback = appCallback;
        this.severKeyboard = keyboard;
    }

    @Override
    protected void setKeyboardRobot(KeyboardRobotExecutor robotExecutor){
        severKeyboard.setRobot(robotExecutor);
    }

    @Override
    protected void startManagers() {
        serverNetwork.start();
	}

    @Override
    protected NetworkHandler initializeNetworkHandler(ErrorListener errorListener) {
        return new ServerNetworkHandler(serverNetwork, gui, serverMouse, severKeyboard, errorListener);
    }

    @Override
    protected GuiHandler initializeGuiHandler(ErrorListener errorListener) {
        return new ServerGuiHandler(serverNetwork, gui, mouse, appCallback, errorListener);
    }

    @Override
    protected MouseHandler initializeMouseHandler(ErrorListener errorListener) {
        ServerMouseHandler handler = new ServerMouseHandler(serverNetwork, gui, errorListener);
        serverMouse.setListener(handler);
        return handler;
    }

    @Override
    protected KeyboardHandler initializeKeyboardHandler(ErrorListener errorListener) {
        ServerKeyboardHandler handler = new ServerKeyboardHandler(serverNetwork, gui, errorListener);
        severKeyboard.setListener(handler);
        return handler;
    }
}
