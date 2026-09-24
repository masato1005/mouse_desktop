package common.main;

import common.gui.GuiManager;
import common.gui.handler.GuiHandler;
import common.keyboard.KeyboardManager;
import common.keyboard.handler.KeyboardHandler;
import common.main.handler.ErrorHandler;
import common.mouse.MouseManager;
import common.mouse.handler.MouseHandler;
import common.network.NetworkManager;
import common.network.handler.NetworkHandler;
import common.robot.KeyboardRobotExecutor;
import common.robot.RobotExecutor;

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
		NetworkHandler networkHandler = initializeNetworkHandler(errorHandler);
		GuiHandler guiHandler = initializeGuiHandler(errorHandler);
		MouseHandler mouseHandler = initializeMouseHandler(errorHandler);
		KeyboardHandler keyboardHandler = initializeKeyboardHandler(errorHandler);

		RobotExecutor robotExecutor = new RobotExecutor(errorHandler);
		robotExecutor.start();
		if(robotExecutor.isRobotNull())
			return;

		network.setEventListener(networkHandler);
		gui.setEventListener(guiHandler);
		mouse.setEventListener(mouseHandler);
		keyboard.setEventListener(keyboardHandler);

		network.setErrorListener(errorHandler);
		gui.setErrorListener(errorHandler);
		mouse.setErrorListener(errorHandler);
		keyboard.setErrorListener(errorHandler);

		mouse.setRobotExecuter(robotExecutor);
		setKeyboardRobot(robotExecutor);
	}

	protected void setKeyboardRobot(KeyboardRobotExecutor robotExecutor){}

	protected abstract NetworkHandler initializeNetworkHandler(ErrorListener errorListener);

	protected abstract GuiHandler initializeGuiHandler(ErrorListener errorListener);

	protected abstract MouseHandler initializeMouseHandler(ErrorListener errorListener);

	protected abstract KeyboardHandler initializeKeyboardHandler(ErrorListener errorListener);
	
	protected abstract void startManagers();
}
