package main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Event.GuiEvent;
import Event.NetworkEvent;
import Event.OriginalMouseEvent;
import EventType.AppType;
import EventType.DataType;
import EventType.MouseEventType;
import EventType.WallType;
import Json.InputConvertedData;
import Json.JsonConverter;
import Keyboard.KeyboardManager;
import Listener.GuiEventListener;
import Listener.KeyboardEventListener;
import Listener.MouseEventListener;
import Listener.NetworkEventListener;
import Mouse.MouseManager;
import data.MouseData;
import gui.GuiManager;
import gui.contents.ErrorExitGui;
import network.NetworkManager;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public abstract class MainController
		implements NetworkEventListener, GuiEventListener, MouseEventListener, KeyboardEventListener {
	private final NetworkManager network;
	private final GuiManager gui;
	private final MouseManager mouse;
	private final KeyboardManager keyboard;

	private final JsonConverter jsonConverter = new JsonConverter();
	private final ObjectMapper mapper = new ObjectMapper();

	private Boolean updating = true;

	public MainController(int portNumber, NetworkManager network, GuiManager gui, MouseManager mouse,
			KeyboardManager keyboard) {
		this.network = network;
		this.gui = gui;
		this.mouse = mouse;
		this.keyboard = keyboard;
	}

	public void start() {
		initializeListeners();
		gui.showChoiceServerOrClient();
	}

	private void initializeListeners() {
		network.setListener(this);
		gui.setListener(this);
		mouse.setListener(this);
		keyboard.setListener(this);
	}

	@Override
	public void onGuiEvent(GuiEvent e) {
		switch (e.getType()) {
			case CHOOSE_APPTYPE -> {
				setAppType((AppType) e.getData());
				network.start();
			}
			case STOP_UDP_SERVER -> {
				network.stopUdpServer();
				gui.showChoiceServerOrClient();
			}
			case RETRY -> {
				network.setTimeout(false);
				network.start();
				new ErrorExitGui("再検索が正しく行われませんでした。");
			}
			case CHOOSE_WALL -> {
				mouse.setScannerWallType((WallType) e.getData());
				network.sendData(jsonConverter.dataConverter(DataType.WALL_TYPE, (WallType) e.getData()));
			}
			case PUSH_SUCCESS_OK -> {
				if (appType == AppType.CLIENT) {
					gui.viewChoiceWallTypeGui();
				}
			}
			case MOVE_WHEEL -> mouse.moveWheel((int) e.getData());
			case LEFT_CLICK -> mouse.clickMouse(MouseEventType.LEFT_CLICK, (boolean) e.getData());
			case WHEEL_CLICK -> mouse.clickMouse(MouseEventType.WHEEL_CLICK, (boolean) e.getData());
			case RIGHT_CLICK -> mouse.clickMouse(MouseEventType.RIGHT_CLICK, (boolean) e.getData());
			case SYSTEM_EXIT -> network.systemExit();
			default -> {
			}
		}
	}

	@Override
	public void onNetworkEvent(NetworkEvent e) {
		switch (e.getType()) {
			case WAITING_CLIENT -> gui.showWaitingServerGui();
			case TIMEOUT -> {
				network.setTimeout(true);
				gui.showTimeout();
			}
			case SUCCESSCONNECT -> {
				switch (appType) {
					case SERVER -> {
						gui.removeWaitingServer();
						gui.initInvisibleWindow();
						gui.openInvisibleWindow();
						gui.successConnectGui();
					}
					case CLIENT -> {
						gui.initInvisibleWindow();
						mouse.start();
						gui.successConnectGui();
					}
				}
				update();
			}
			case RECEIVEDATA -> {
				InputConvertedData data = (InputConvertedData) e.getData();
				try {
					switch (appType) {
						case SERVER -> {
							switch (data.getDataType()) {
								case MOUSE -> mouse.updateDrawer(mapper.treeToValue(data.getData(), MouseData.class));
								case WALL_TYPE ->
									mouse.setDrawerWallType(mapper.treeToValue(data.getData(), WallType.class));
								case SYSTEM_EXIT -> System.exit(0);
								default -> {
								}
							}
						}
						case CLIENT -> {
							switch (data.getDataType()) {
								case MOUSE -> {
									MouseData mouseData = mapper.treeToValue(data.getData(), MouseData.class);
									if (mouseData.getMouseEventType() == MouseEventType.TOUCH_WALL) {
										mouse.returnMouseToClient(mouseData);
									}
								}
								case SYSTEM_EXIT -> System.exit(0);
								default -> {
								}
							}
						}
					}

				} catch (JsonProcessingException | IllegalArgumentException e1) {
					new ErrorExitGui("Jsonへの変換でエラーが発生しました");
				}
			}
			case ERROR -> updating = false;
		}
	}

	public void update() {
		while (updating) {
			network.loop();
		}
	}

	@Override
	public void onMouseEvent(OriginalMouseEvent e) {
		switch (e.getType()) {
			case MOVE -> network.sendData(e.getData());
			case SEND_MOUSE -> gui.openInvisibleWindow();
			case CLOSE_INVISIBLE_WINDOW -> gui.closeInvisibleWindow();
			default -> {
			}
		}
	}
}
