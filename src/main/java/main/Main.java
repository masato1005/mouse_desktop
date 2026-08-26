package main;

/*





*/

import java.io.IOException;

import EventType.AppType;
import Keyboard.KeyboardManager;
import Mouse.MouseManager;
import gui.GUIManager;
import gui.contents.ErrorExitGUI;
import gui.contents.choiceServerORClient;
import network.NetworkManager;
import platform.WindowsExitHotkey;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class Main {
	final int portNumber = 5000;
	AppType apptype = null;

	NetworkManager network;
	MainController controller;
	GUIManager gui;
	MouseManager mouse;
	KeyboardManager keyboard;
	WindowsExitHotkey exitHotkey;

	public void start() {
		gui = new GUIManager();
	network = new NetworkManager(portNumber);
	mouse = new MouseManager();
	keyboard = new KeyboardManager();
	controller = new MainController(portNumber, this, network, gui, mouse, keyboard);
		startExitHotkey();

		new choiceServerORClient(gui.getListener());
	}

	private void startExitHotkey() {
		if (exitHotkey == null) {
			exitHotkey = new WindowsExitHotkey(this::forceExit);
			exitHotkey.start();
		}
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

	public void setAppType(AppType apptype) {
		this.apptype = apptype;

		gui.setAppType(apptype);
		network.setAppType(apptype);
		controller.setAppType(apptype);

		try {
			controller.start();
		} catch (IOException e) {
			new ErrorExitGUI("アプリの初期化に失敗しました");
		}
	}

	public static void main(String[] args) {
		Main start = new Main();
		try {
			start.start();
		} catch (Exception e) {
			new ErrorExitGUI("アプリの起動に失敗しました");
		}
	}
}
