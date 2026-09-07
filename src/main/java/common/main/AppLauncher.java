package common.main;

/*
接続時のランダムトークンによる認証の実装（最後に行う）




*/

import client.ClientController;
import client.mouse.ClientMouseManager;
import client.network.ClientNetworkManager;
import common.keyboard.KeyboardManager;
import common.gui.GuiManager;
import common.gui.contents.ChoiceServerOrClient;
import common.main.handler.StartupErrorHandler;
import common.platform.WindowsExitHotkey;
import server.ServerController;
import server.mouse.ServerMouseManager;
import server.network.ServerNetworkManager;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class AppLauncher implements AppCallback {
	final int portNumber = 5000;
	GuiManager gui = new GuiManager();
	WindowsExitHotkey exitHotkey;
	private final ErrorListener startupErrorListener = new StartupErrorHandler(gui);

	public static void main(String[] args) {
		AppLauncher start = new AppLauncher();
		try {
			start.start();
		} catch (Exception e) {
			start.startupErrorListener.happenError("アプリの起動に失敗しました");
		}
	}

	public void start() {
		new ChoiceServerOrClient(this, startupErrorListener);
	}

	public void chooseServer() {
		createServerManagers();
	}

	private void createServerManagers() {
		if (gui == null)
			gui = new GuiManager();
		ServerNetworkManager network = new ServerNetworkManager(portNumber);
		ServerMouseManager mouse = new ServerMouseManager();
		KeyboardManager keyboard = new KeyboardManager();
		ServerController controller = new ServerController(portNumber, network, gui, mouse, keyboard, this);
		if (exitHotkey == null) {
			exitHotkey = new WindowsExitHotkey(this::forceExit);
			exitHotkey.start();
		}
		controller.start();
	}

	public void chooseClient() {
		createClientManagers();
	}

	private void createClientManagers() {
		if (gui == null)
			gui = new GuiManager();
		ClientNetworkManager network = new ClientNetworkManager(portNumber);
		ClientMouseManager mouse = new ClientMouseManager();
		KeyboardManager keyboard = new KeyboardManager();
		ClientController controller = new ClientController(portNumber, network, gui, mouse, keyboard);
		if (exitHotkey == null) {
			exitHotkey = new WindowsExitHotkey(this::forceExit);
			exitHotkey.start();
		}
		controller.start();
	}

	private void forceExit() {
		try {
			gui.getListener().systemExit();
		} catch (RuntimeException e) {
			System.err.println("終了通知の送信に失敗しました: " + e.getMessage());
		} finally {
			System.exit(0);
		}
	}

	@Override
	public void returnToAppSelection() {
		new ChoiceServerOrClient(this, startupErrorListener);
	}

}
