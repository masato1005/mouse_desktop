package common.main.handler;

import common.keyboard.KeyboardManager;
import common.gui.GuiManager;
import common.main.ErrorListener;
import common.mouse.MouseManager;
import common.network.NetworkManager;

public class ErrorHandler implements ErrorListener {
    private final NetworkManager network;
    private final GuiManager gui;
    private final MouseManager mouse;
    private final KeyboardManager keyboard;

    public ErrorHandler(NetworkManager network, GuiManager gui, MouseManager mouse, KeyboardManager keyboard) {
        this.network = network;
        this.gui = gui;
        this.mouse = mouse;
        this.keyboard = keyboard;
    }

    @Override
    public void happenError(String errorMassage) {
        network.errorHandle();
        gui.errorHandle(errorMassage);
        mouse.errorHandle();
        keyboard.errorHandle();
    }
}
