package common.gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.SwingUtilities;

import common.Listener.GuiEventListener;
import common.Listener.GuiListener;
import common.Listener.implemented.ImplementedGuiListener;
import common.gui.contents.ChoiceWarpWall;
import common.gui.contents.ErrorExitGui;
import common.gui.contents.InvisibleWindow;
import common.gui.contents.SuccessConnectGui;
import common.gui.contents.TimeoutGui;
import common.gui.contents.WaitingServerGui;
import common.main.ErrorHandle;
import common.main.ErrorListener;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class GuiManager implements ErrorHandle {
	private WaitingServerGui waitingServerGui;
	private final ImplementedGuiListener listener = new ImplementedGuiListener();
	InvisibleWindow invisibleWindow = null;

	private ErrorListener errorListener;

	public void setErrorListener(ErrorListener errorListener) {
		this.errorListener = errorListener;
	}

	public void setEventListener(GuiEventListener eventListener) {
		listener.setListener(eventListener);
	}

	public GuiListener getListener() {
		return listener;
	}

	public void showWaitingServerGui() {
		SwingUtilities.invokeLater(() -> {
			waitingServerGui = new WaitingServerGui(listener);
		});
	}

	public void removeWaitingServer() {
		SwingUtilities.invokeLater(() -> {
			waitingServerGui.dispose();
		});

	}

	public void successConnectGui() {
		SwingUtilities.invokeLater(() -> {
			new SuccessConnectGui(listener);
		});
	}

	public void viewChoiceWallTypeGui() {
		SwingUtilities.invokeLater(() -> {
			new ChoiceWarpWall(listener);
		});

	}

	public void showTimeout() {
		SwingUtilities.invokeLater(() -> {
			new TimeoutGui(listener);
		});

	}

	public void initInvisibleWindow() {
		System.out.println(2);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		SwingUtilities.invokeLater(() -> {
			invisibleWindow = new InvisibleWindow(screenSize.width, screenSize.height, listener);
		});

	}

	public void openInvisibleWindow() {
		if (invisibleWindow == null) {
			SwingUtilities.invokeLater(() -> {
				initInvisibleWindow();
			});

		}
		SwingUtilities.invokeLater(() -> {
			invisibleWindow.openWindow();
		});

	}

	public void closeInvisibleWindow() {
		if (invisibleWindow != null) {
			SwingUtilities.invokeLater(() -> {
				invisibleWindow.closeWindow();
			});
		}
	}

	@Override
	public void errorHandle() {
		SwingUtilities.invokeLater(() -> {
			new ErrorExitGui("エラーが発生しました");
		});

	}

	public void errorHandle(String errorMassage) {
		SwingUtilities.invokeLater(() -> {
			new ErrorExitGui(errorMassage);
		});
	}
}
