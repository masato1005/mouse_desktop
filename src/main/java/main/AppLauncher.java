package main;

/*





*/

import Keyboard.KeyboardManager;
import Mouse.MouseManager;
import gui.GuiManager;
import gui.contents.ErrorExitGui;
import network.NetworkManager;
import platform.WindowsExitHotkey;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class AppLauncher {
	final int portNumber = 5000;

	NetworkManager network;
	MainController controller;
	GuiManager gui;
	MouseManager mouse;
	KeyboardManager keyboard;
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
		createManagers();
		startProcessing();
	}

	private void createManagers() {
		gui = new GuiManager();
		network = new NetworkManager(portNumber);
		mouse = new MouseManager();
		keyboard = new KeyboardManager();
		controller = new MainController(portNumber, network, gui, mouse, keyboard);
		exitHotkey = new WindowsExitHotkey(this::forceExit);
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
}
