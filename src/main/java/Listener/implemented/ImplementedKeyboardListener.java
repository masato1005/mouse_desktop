package Listener.implemented;

import Listener.KeyboardEventListener;
import Listener.KeyboardListener;

public class ImplementedKeyboardListener implements KeyboardListener {
	private KeyboardEventListener eventListener;

	public void setListener(KeyboardEventListener eventListener) {
		this.eventListener = eventListener;
	}
}
