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
import Handler.KeyboardHandler;
import Handler.MouseHandler;
import Handler.NetworkHandler;
import Json.InputConvertedData;
import Json.JsonConverter;
import Json.MouseData;
import Keyboard.KeyboardManager;
import Listener.GUIEventListener;
import Listener.KeyboardEventListener;
import Listener.MouseEventListener;
import Listener.NetworkEventListener;
import Mouse.MouseManager;
import gui.GUIManager;
import gui.contents.ErrorExitGUI;
import network.NetworkManager;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class MainController implements NetworkEventListener, GUIEventListener, MouseEventListener, KeyboardEventListener {
	private final NetworkManager network;
	private final NetworkHandler netHandler;
	private final GUIManager gui;
	private final GUIHandler guiHandler;
	private final MouseManager mouse;
	private final MouseHandler mouseHandler;
	private final KeyboardManager keyboard;
	private final KeyboardHandler keyboardHandler;
	private final Main main;
	private AppType appType;

	private final JsonConverter jsonConverter = new JsonConverter();
	private ObjectMapper mapper = new ObjectMapper();

	private Boolean updating = true;

	public MainController(int portNumber, Main main, NetworkManager network, GUIManager gui, MouseManager mouse,
			KeyboardManager keyboard) {
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

		this.keyboard = keyboard;
		keyboardHandler = new KeyboardHandler();
		keyboardHandler.setEventListener(this);
		keyboard.setListener(keyboardHandler);
	}

	public void start() throws IOException {
		network.start();
	}

	public void update() throws IOException {
		while (updating) {
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
					new ErrorExitGUI("正しくを起動できませんでした");
				}
			}
			case SUCCESSCONNECT -> {
				switch (appType) {
					case SERVER -> {
                                            gui.removeWaitingServer();
                                            System.out.println(1);
                                            gui.initInvisibleWindow();
                                            gui.openInvisibleWindow();
                                            gui.successConnectGUI();
                        }
					case CLIENT -> {
                                            gui.initInvisibleWindow();
                                            mouse.start();
                                            gui.successConnectGUI();
                        }
				}
				try {
					update();
				} catch (IOException e1) {
					new ErrorExitGUI("接続処理が正しく処理されませんでした");
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
					new ErrorExitGUI("Jsonへの変換でエラーが発生しました");
				}
			}
			case ERROR -> updating = false;
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
					new ErrorExitGUI("再検索が正しく行われませんでした。");
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
