package main;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Event.GUIEvent;
import Event.NetworkEvent;
import Event.OriginalMouseEvent;
import EventType.AppType;
import EventType.DataType;
import EventType.MouseEventType;
import EventType.WallType;
import Handler.GUIHandler;
import Handler.MouseHandler;
import Handler.NetworkHandler;
import Json.InputConvertedData;
import Json.JsonConverter;
import Json.MouseData;
import Listener.GUIEventListener;
import Listener.MouseEventListener;
import Listener.NetworkEventListener;
import Mouse.MouseManager;
import gui.GUIManager;
import network.NetworkManager;

public class MainController implements NetworkEventListener, GUIEventListener, MouseEventListener {
	private NetworkManager network;
	private NetworkHandler netHandler;
	private GUIManager gui;
	private GUIHandler guiHandler;
	private MouseManager mouse;
	private MouseHandler mouseHandler;
	private Main main;
	private AppType appType;

	private JsonConverter jsonConverter = new JsonConverter();
	private ObjectMapper mapper = new ObjectMapper();

	public MainController(int portNumber, Main main, NetworkManager network, GUIManager gui, MouseManager mouse) {
		this.network = network;
		netHandler = new NetworkHandler();
		netHandler.setEventListener(this);
		network.setListener(netHandler);
		this.main = main;

		this.gui = gui;
		guiHandler = new GUIHandler();
		guiHandler.setEventListener(main, this);
		gui.setListener(guiHandler);

		this.mouse = mouse;
		mouseHandler = new MouseHandler();
		mouseHandler.setEventListener(this);
		mouse.setListener(mouseHandler);
	}

	public void start() throws IOException {
		network.start();
	}

	public void update() throws IOException {
		while (true) {
			network.loop();
		}
	}

	public void setAppType(AppType appType) {
		this.appType = appType;
	}

	@Override
	public void onNetworkEvent(NetworkEvent e) {
		switch (e.getType()) {
			case SEARCH -> gui.showWaitingServerGUI();
			case TIMEOUT -> {
				network.setTimeout(true);
				gui.showTimeout();
			}
			case CHOICESC -> {
				try {
					main.start();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			case SUCCESSCONNECT -> {
				switch (appType) {
					case SERVER:
						gui.removeWaitingServer();
						System.out.println(1);
						gui.initInvisibleWindow();
						gui.openInvisibleWindow();
						gui.successConnectGUI();
						break;
					case CLIENT:
						gui.initInvisibleWindow();
						mouse.start();
						gui.successConnectGUI();
						break;
				}
				try {
					update();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			case RECEIVEDATA -> {
				InputConvertedData data = (InputConvertedData) e.getData();
				try {
					switch (appType) {
						case SERVER -> {
							switch (data.getDataType()) {
								case MOUSE -> mouse.updateDrawer(mapper.treeToValue(data.getData(), MouseData.class));
								case WALLTYPE -> mouse.setDrawerWallType(mapper.treeToValue(data.getData(), WallType.class));
								case SYSTEMEXIT -> System.exit(0);
								default -> {
                                                }
							}
						}
						case CLIENT -> {
							switch(data.getDataType()){
								case MOUSE -> {
									MouseData mouseData = mapper.treeToValue(data.getData(), MouseData.class);
									if (mouseData.getMouseEventType() == MouseEventType.TOUCHWALL) {
										mouse.returnMouseToClient(mouseData);
									}
								}
								case SYSTEMEXIT -> System.exit(0);
								default -> {
									
								}
									
							}
						}
					}

				} catch (JsonProcessingException | IllegalArgumentException e1) {
					e1.printStackTrace();
				}
			}

		}
	}

	@Override
	public void onGUIEvent(GUIEvent e) {
		switch (e.getType()) {
			case STOPSERVER:
				network.stopUdpServer();
				break;
			case RETRY:
				try {
					network.setTimeout(false);
					network.start();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				break;
			case CHOICEWLL:
				mouse.setScannerWallType((WallType) e.getData());
				network.sendData(jsonConverter.dataConverter(DataType.WALLTYPE, (WallType) e.getData()));
				break;
			case PUSHSUCCESSOK:
				if (appType == AppType.CLIENT) {
					gui.viewChoiceWallTypeGui();
				}
				break;
			case MOVEWHEEL:
				mouse.moveWheel((int) e.getData());
				break;
			case LEFTCLICK:
				mouse.clickMouse(MouseEventType.LEFTCLICK, (boolean) e.getData());
				break;
			case WHEELCLICK:
				mouse.clickMouse(MouseEventType.WHEELCLICK, (boolean) e.getData());
				break;
			case RIGHTCLICK:
				mouse.clickMouse(MouseEventType.RIGHTCLICK, (boolean) e.getData());
				break;
			case SystemExit:
				network.systemExit();
			default:
				break;
		}
	}

	@Override
	public void onMouseEvent(OriginalMouseEvent e) {
		switch (e.getType()) {
			case MOVE -> network.sendData(e.getData());
			case SENDMOUSE -> gui.openInvisibleWindow();
			case CLOSEINVISIBLEWINDOW -> gui.closeInvisibleWindow();
			default -> {
                }
		}
	}

}
