package gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import Listener.GuiEventListener;
import Listener.GuiListener;
import Listener.implemented.ImplementedGuiListener;
import gui.contents.ChoiceServerOrClient;
import gui.contents.ChoiceWarpWall;
import gui.contents.InvisibleWindow;
import gui.contents.SuccessConnectGui;
import gui.contents.TimeoutGui;
import gui.contents.WaitingServerGui;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class GuiManager {
	private WaitingServerGui waitingServerGui;
	private final ImplementedGuiListener listener = new ImplementedGuiListener();
	InvisibleWindow invisibleWindow = null;

	public void setListener(GuiEventListener eventListener) {
		listener.setListener(eventListener);
	}

    public GuiListener getListener() {
        return listener;
    }

	public void showChoiceServerOrClient(){
		new ChoiceServerOrClient(listener);
	}

	public void showWaitingServerGui() {
		waitingServerGui = new WaitingServerGui(listener);
	}
	
	public void removeWaitingServer() {
		waitingServerGui.dispose();
	}
	
	public void successConnectGui() {
		new SuccessConnectGui(listener);
	}

    public void viewChoiceWallTypeGui() {
		new ChoiceWarpWall(listener);
    }

	public void showTimeout() {
		new TimeoutGui(listener);
	}

    public void initInvisibleWindow() {
		System.out.println(2);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		invisibleWindow = new InvisibleWindow(screenSize.width, screenSize.height, listener);
    }

    public void openInvisibleWindow() {
		if (invisibleWindow == null) {
			initInvisibleWindow();
		}
		invisibleWindow.openWindow();

    }

    public void closeInvisibleWindow() {
		if (invisibleWindow != null) {
			invisibleWindow.closeWindow();
		}
    }
}
