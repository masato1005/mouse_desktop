package common.main;

import com.fasterxml.jackson.databind.ObjectMapper;

import common.Event.GuiEvent;
import common.Event.NetworkEvent;
import common.Event.OriginalMouseEvent;
import common.EventType.DataType;
import common.EventType.MouseEventType;
import common.EventType.WallType;
import common.Json.InputConvertedData;
import common.Json.JsonConverter;
import common.Keyboard.KeyboardManager;
import common.Listener.GuiEventListener;
import common.Listener.KeyboardEventListener;
import common.Listener.MouseEventListener;
import common.Listener.NetworkEventListener;
import common.Mouse.MouseManager;
import common.gui.GuiManager;
import common.gui.contents.ErrorExitGui;
import common.network.NetworkManager;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public abstract class Controller
		implements NetworkEventListener, GuiEventListener, MouseEventListener, KeyboardEventListener {
	protected final NetworkManager network;
	protected final GuiManager gui;
	protected final MouseManager mouse;
	protected final KeyboardManager keyboard;

	protected final JsonConverter jsonConverter = new JsonConverter();
	protected final ObjectMapper mapper = new ObjectMapper();

	protected Boolean updating = true;

	public Controller(int portNumber, NetworkManager network, GuiManager gui, MouseManager mouse,
			KeyboardManager keyboard) {
		this.network = network;
		this.gui = gui;
		this.mouse = mouse;
		this.keyboard = keyboard;
	}

	public void start() {
		initializeListeners();
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
				network.start();
			}
			case STOP_UDP_SERVER -> {
				stopUdpServer();
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
			case PUSH_SUCCESS_OK -> viewChoiceWallTypeGui();

			case MOVE_WHEEL -> mouse.moveWheel((int) e.getData());
			case LEFT_CLICK -> mouse.clickMouse(MouseEventType.LEFT_CLICK, (boolean) e.getData());
			case WHEEL_CLICK -> mouse.clickMouse(MouseEventType.WHEEL_CLICK, (boolean) e.getData());
			case RIGHT_CLICK -> mouse.clickMouse(MouseEventType.RIGHT_CLICK, (boolean) e.getData());
			case SYSTEM_EXIT -> network.systemExit();
			default -> {
			}
		}
	}

	protected void stopUdpServer() {
	}

	protected void viewChoiceWallTypeGui() {
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
				successConnect();
			}
			case RECEIVEDATA -> {
				InputConvertedData data = (InputConvertedData) e.getData();

				receiveData(data);

			}
			case ERROR -> updating = false;
		}
	}

	protected void successConnect() {
	}

	protected void receiveData(InputConvertedData data) {
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
