package main;

/*





*/

import Keyboard.KeyboardManager;
import Mouse.MouseManager;
import gui.GuiManager;
import gui.contents.ChoiceServerOrClient;
import gui.contents.ErrorExitGui;
import network.ClientManager;
import network.ServerManager;
import platform.WindowsExitHotkey;

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
		ClientManager network = new ClientManager(portNumber);
		MouseManager mouse = new MouseManager();
		KeyboardManager keyboard = new KeyboardManager();
		controller = new ClientController(portNumber, network, gui, mouse, keyboard);
		exitHotkey = new WindowsExitHotkey(this::forceExit);
	}

	private void createServerManagers() {
		gui = new GuiManager();
		ServerManager network = new ServerManager(portNumber);
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
