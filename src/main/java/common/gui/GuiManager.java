package common.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import common.Listener.GuiEventListener;
import common.Listener.GuiListener;
import common.Listener.implemented.ImplementedGuiListener;
import common.gui.contents.ChoiceWarpWall;
import common.gui.contents.InvisibleWindow;
import common.gui.contents.SuccessConnectGui;
import common.gui.contents.TimeoutGui;
import common.gui.contents.WaitingServerGui;
import common.network.ErrorCallback;
import common.network.ThreadStopper;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class GuiManager implements ErrorCallback, ThreadStopper {
	private WaitingServerGui waitingServerGui;
	private final ImplementedGuiListener listener = new ImplementedGuiListener();
	InvisibleWindow invisibleWindow = null;

	private final BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();
	private boolean threadRunning = false;
	private boolean acceptAddTask = false;

	private final Thread guiThread = new Thread(() -> {
		while (threadRunning) {
			try {
				Runnable task = tasks.take();
				task.run();
				if (!threadRunning)
					break;
			} catch (InterruptedException e) {
				break;
			}
		}
	});

	private void threadStart() {
		threadRunning = true;
		acceptAddTask = true;
		guiThread.start();
	}

	private void threadStop() {
		threadRunning = false;
		acceptAddTask = false;
		tasks.clear();
		guiThread.interrupt();
	}

	private void addTask(Runnable task) {
		if (acceptAddTask)
			tasks.add(task);
	}

	public void setListener(GuiEventListener eventListener) {
		listener.setListener(eventListener);
	}

    public GuiListener getListener() {
        return listener;
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

    @Override
	public void happenError() {
		threadStop();
	}

	@Override
	public void threadStopper() {
		threadStop();
	}
}
