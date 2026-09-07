package common.main;

import common.keyboard.KeyboardManager;
import common.keyboard.handler.KeyboardHandler;
import common.gui.GuiManager;
import common.gui.handler.GuiHandler;
import common.main.handler.ErrorHandler;
import common.mouse.MouseManager;
import common.mouse.handler.MouseHandler;
import common.network.NetworkManager;
import common.network.handler.NetworkHandler;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public abstract class Controller {
	protected final NetworkManager network;
	protected final GuiManager gui;
	protected final MouseManager mouse;
	protected final KeyboardManager keyboard;

	public Controller(int portNumber, NetworkManager network, GuiManager gui, MouseManager mouse,
			KeyboardManager keyboard) {
		this.network = network;
		this.gui = gui;
		this.mouse = mouse;
		this.keyboard = keyboard;
	}

	public void start() {
		initializeListeners();
		startManagers();
	}

	private void initializeListeners() {
		ErrorHandler errorHandler = new ErrorHandler(network, gui, mouse, keyboard);
		NetworkHandler networkHandler = createNetworkHandler(errorHandler);
		GuiHandler guiHandler = createGuiHandler(errorHandler);
		MouseHandler mouseHandler = new MouseHandler(network, gui, errorHandler);
		KeyboardHandler keyboardHandler = new KeyboardHandler();

		network.setEventListener(networkHandler);
		gui.setEventListener(guiHandler);
		mouse.setEventListener(mouseHandler);
		keyboard.setEventListener(keyboardHandler);

		network.setErrorListener(errorHandler);
		gui.setErrorListener(errorHandler);
		mouse.setErrorListener(errorHandler);
		keyboard.setErrorListener(errorHandler);
	}

	protected abstract NetworkHandler createNetworkHandler(ErrorListener errorListener);

	protected abstract GuiHandler createGuiHandler(ErrorListener errorListener);

	protected void startManagers() {
	}

}
