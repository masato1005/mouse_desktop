package Listener.implemented;

import Handler.GUIHandler;
import Listener.GUIListener;

public class ImplementedGUIListener implements GUIListener {
    private GUIHandler guiHandler;

    public void setListener(GUIHandler guiHandler) {
		this.guiHandler = guiHandler;
	}
    
    @Override
    public void choiseServer() {
        guiHandler.server();
    }

    @Override
    public void choiseClient() {
        guiHandler.client();
    }

    @Override
    public void pushStop() {
        guiHandler.stopServer();
    }

    @Override
    public void retry() {
        guiHandler.retry();
    }

    @Override
    public void choiceNorthWall() {
        guiHandler.choiceNorthWall();
    }

    @Override
    public void choiceSouthWall() {
        guiHandler.choiceSouthWall();
    }

    @Override
    public void choiceWestWall() {
        guiHandler.choiceWestWall();
    }

    @Override
    public void choiceEastWall() {
        guiHandler.choiceEastWall();
    }

    @Override
    public void pushedSuccessOkButton() {
        guiHandler.pushedSuccessOkButton();
    }

    @Override
    public void moveWheel(int amount) {
        guiHandler.moveWheel(amount);
    }

    @Override
    public void clickLeftMouse(boolean pressed) {
        guiHandler.clickLeftMouse(pressed);
    }

    @Override
    public void clickWheelMouse(boolean pressed) {
        guiHandler.clickWheelMouse(pressed);
    }

    @Override
    public void clickRightMouse(boolean pressed) {
        guiHandler.clickRightMouse(pressed);
    }

    @Override
    public void systemExit(){
        guiHandler.systemExit();
    }

}
