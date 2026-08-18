package main;

/*





*/

import java.io.IOException;

import EventType.AppType;
import Mouse.MouseManager;
import gui.GUIManager;
import gui.contents.ErrorExitGUI;
import gui.contents.choiceServerORClient;
import network.NetworkManager;

public class Main {
	int portNumber = 5000;
	AppType apptype = null;

	NetworkManager network;
	MainController controller;
	GUIManager gui;
	MouseManager mouse;

	public void start() {
		gui = new GUIManager();
		network = new NetworkManager(portNumber);
		mouse = new MouseManager();
		controller = new MainController(portNumber, this, network, gui, mouse);

		new choiceServerORClient(gui.getListener());
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
