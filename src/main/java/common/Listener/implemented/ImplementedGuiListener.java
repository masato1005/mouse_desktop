package common.Listener.implemented;

import common.Event.GuiEvent;
import common.EventType.AppType;
import common.EventType.GuiEventType;
import common.EventType.WallType;
import common.Listener.GuiEventListener;
import common.Listener.GuiListener;

public class ImplementedGuiListener implements GuiListener {
    private GuiEventListener eventListener;

    public void setListener(GuiEventListener eventListener) {
		this.eventListener = eventListener;
	}
    
    @Override
    public void chooseServer() {
        eventListener.onGuiEvent(new GuiEvent(GuiEventType.CHOOSE_APPTYPE, AppType.SERVER));
    }

    @Override
    public void chooseClient() {
        eventListener.onGuiEvent(new GuiEvent(GuiEventType.CHOOSE_APPTYPE, AppType.CLIENT));
    }

    @Override
    public void pushedSuccessOkButton() {
        eventListener.onGuiEvent(new GuiEvent(GuiEventType.PUSH_SUCCESS_OK, null));
    }

    @Override
    public void choiceNorthWall() {
        eventListener.onGuiEvent(new GuiEvent(GuiEventType.CHOOSE_WALL, WallType.NORTH));
    }

    @Override
    public void choiceSouthWall() {
        eventListener.onGuiEvent(new GuiEvent(GuiEventType.CHOOSE_WALL, WallType.SOUTH));
    }

    @Override
    public void choiceWestWall() {
        eventListener.onGuiEvent(new GuiEvent(GuiEventType.CHOOSE_WALL, WallType.WEST));
    }

    @Override
    public void choiceEastWall() {
        eventListener.onGuiEvent(new GuiEvent(GuiEventType.CHOOSE_WALL, WallType.EAST));
    }

    @Override
    public void pushStop() {
        eventListener.onGuiEvent(new GuiEvent(GuiEventType.STOP_UDP_SERVER, null));
    }

    @Override
    public void retry() {
        eventListener.onGuiEvent(new GuiEvent(GuiEventType.RETRY, null));
    }

    @Override
    public void moveWheel(int amount) {
        eventListener.onGuiEvent(new GuiEvent(GuiEventType.MOVE_WHEEL, amount));
    }

    @Override
    public void clickLeftMouse(boolean pressed) {
        eventListener.onGuiEvent(new GuiEvent(GuiEventType.LEFT_CLICK, pressed));
    }

    @Override
    public void clickWheelMouse(boolean pressed) {
        eventListener.onGuiEvent(new GuiEvent(GuiEventType.WHEEL_CLICK, pressed));
    }

    @Override
    public void clickRightMouse(boolean pressed) {
        eventListener.onGuiEvent(new GuiEvent(GuiEventType.RIGHT_CLICK, pressed));
    }

    @Override
    public void systemExit(){
        eventListener.onGuiEvent(new GuiEvent(GuiEventType.SYSTEM_EXIT, null));
    }

}
