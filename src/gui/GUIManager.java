package gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import EventType.AppType;
import Handler.GUIHandler;
import Listener.GUIListener;
import Listener.implemented.ImplementedGUIListener;
import gui.contents.ChoiceWarpWall;
import gui.contents.InvisibleWindow;
import gui.contents.SuccessConnectGUI;
import gui.contents.TimeoutGUI;
import gui.contents.WaitingServerGUI;

public class GUIManager {
	private WaitingServerGUI waitingServerGUI;
	private ImplementedGUIListener listener = new ImplementedGUIListener();
	private AppType appType;
	InvisibleWindow invisibleWindow;

	public void showWaitingServerGUI() {
		waitingServerGUI = new WaitingServerGUI(listener);
	}

	public void showTimeout() {
		new TimeoutGUI(listener);
	}

	public void setListener(GUIHandler guiHandler) {
		listener.setListener(guiHandler);
	}

	public void setAppType(AppType appType) {
		this.appType = appType;
	}
	
	public void successConnectGUI() {
		new SuccessConnectGUI(listener);
	}
	
	public void removeWatingServer() {
		waitingServerGUI.dispose();
	}

    public GUIListener getListener() {
        return listener;
    }

    public void viewChoiceWallTypeGui() {
		new ChoiceWarpWall(listener);
    }

    public void initInvisibleWindow() {
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
