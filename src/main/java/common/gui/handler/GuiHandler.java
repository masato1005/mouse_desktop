package common.gui.handler;

import com.fasterxml.jackson.core.JsonProcessingException;

import common.eventtype.DataType;
import common.eventtype.MouseEventType;
import common.eventtype.WallType;
import common.gui.GuiManager;
import common.gui.listener.GuiListener;
import common.json.JsonConverter;
import common.main.ErrorListener;
import common.mouse.MouseManager;
import common.network.NetworkManager;

public abstract class GuiHandler implements GuiListener {
    protected final NetworkManager network;
    protected final GuiManager gui;
    protected final MouseManager mouse;
    protected final ErrorListener errorListener;

    protected GuiHandler(NetworkManager network, GuiManager gui, MouseManager mouse, ErrorListener errorListener) {
        this.network = network;
        this.gui = gui;
        this.mouse = mouse;
        this.errorListener = errorListener;
    }

    @Override
    public void pushedSuccessOkButton() {
        gui.viewChoiceWallTypeGui();
    }

    @Override
    public void choiceNorthWall() {
        chooseWall(WallType.NORTH);
    }

    @Override
    public void choiceSouthWall() {
        chooseWall(WallType.SOUTH);
    }

    @Override
    public void choiceWestWall() {
        chooseWall(WallType.WEST);
    }

    @Override
    public void choiceEastWall() {
        chooseWall(WallType.EAST);
    }

    protected void chooseWall(WallType wallType) {
        mouse.setWallType(wallType);
        try {
            network.sendData(JsonConverter.toJson(DataType.WALL_TYPE, wallType));
        } catch (JsonProcessingException e) {
            errorListener.happenError("Json処理で不具合が発生しました");
        }

    }

    @Override
    public void moveWheel(int amount) {
        mouse.moveWheel(amount);
    }

    @Override
    public void clickLeftMouse(boolean pressed) {
        mouse.clickMouse(MouseEventType.LEFT_CLICK, pressed);
    }

    @Override
    public void clickWheelMouse(boolean pressed) {
        mouse.clickMouse(MouseEventType.WHEEL_CLICK, pressed);
    }

    @Override
    public void clickRightMouse(boolean pressed) {
        mouse.clickMouse(MouseEventType.RIGHT_CLICK, pressed);
    }

    @Override
    public void systemExit() {
        network.systemExit();
    }

    @Override
    public void pushStop() {
    }

    @Override
    public void retry() {
    }
}
