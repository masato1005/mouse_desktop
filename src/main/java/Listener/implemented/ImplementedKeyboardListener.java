package Listener.implemented;

import Handler.KeyboardHandler;
import Listener.KeyboardListener;

public class ImplementedKeyboardListener implements KeyboardListener {
	private KeyboardHandler handler;

	public void setListener(KeyboardHandler keyboardHandler) {
		this.handler = keyboardHandler;
	}
}
