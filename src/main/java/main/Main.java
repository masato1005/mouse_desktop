package main;

/*





*/

import EventType.AppType;
import Mouse.MouseManager;
import gui.GUIManager;
import gui.contents.choiceServerORClient;
import java.io.IOException;
import network.ConnectionManager;

public class Main {
	int portNumber = 5000;
	AppType apptype = null;

	ConnectionManager network;
	MainController controller;
	GUIManager gui;
	MouseManager mouse;

	public void start() {
		gui = new GUIManager();
		network = new ConnectionManager(portNumber);
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
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Main start = new Main();
		try {
			start.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
