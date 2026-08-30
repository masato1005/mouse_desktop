package common.main;

/*





*/

import common.Keyboard.KeyboardManager;
import common.Mouse.MouseManager;
import common.gui.GuiManager;
import common.gui.contents.ChoiceServerOrClient;
import common.gui.contents.ErrorExitGui;
import client.ClientController;
import client.network.ClientNetworkManager;
import common.platform.WindowsExitHotkey;
import server.ServerController;
import server.network.ServerNetworkManager;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class AppLauncher implements AppCallback{
	final int portNumber = 5000;
	GuiManager gui;
	Controller controller;
	WindowsExitHotkey exitHotkey;

	public static void main(String[] args) {
		AppLauncher start = new AppLauncher();
		try {
			start.start();
		} catch (Exception e) {
			new ErrorExitGui("アプリの起動に失敗しました");
		}
	}

	public void start() {
		new ChoiceServerOrClient(this);
	}

	private void startProcessing() {
		exitHotkey.start();
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

	public void chooseServer() {
		createServerManagers();
		startProcessing();
	}

	public void chooseClient() {
		createClientManagers();
		startProcessing();
	}

	private void createClientManagers() {
		gui = new GuiManager();
		ClientNetworkManager network = new ClientNetworkManager(portNumber);
		MouseManager mouse = new MouseManager();
		KeyboardManager keyboard = new KeyboardManager();
		controller = new ClientController(portNumber, network, gui, mouse, keyboard);
		exitHotkey = new WindowsExitHotkey(this::forceExit);
	}

	private void createServerManagers() {
		gui = new GuiManager();
		ServerNetworkManager network = new ServerNetworkManager(portNumber);
		MouseManager mouse = new MouseManager();
		KeyboardManager keyboard = new KeyboardManager();
		controller = new ServerController(portNumber, network, gui, mouse, keyboard,this);
		exitHotkey = new WindowsExitHotkey(this::forceExit);
	}

	@Override
	public void returnToAppSelection(){
		new ChoiceServerOrClient(this);
	}

}
