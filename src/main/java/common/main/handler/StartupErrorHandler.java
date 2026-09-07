package common.main.handler;

import common.gui.GuiManager;
import common.main.ErrorListener;

public class StartupErrorHandler implements ErrorListener {
    private final GuiManager gui;

    public StartupErrorHandler(GuiManager gui) {
        this.gui = gui;
    }

    @Override
    public void happenError(String errorMassage) {
        gui.errorHandle(errorMassage);
    }
}
