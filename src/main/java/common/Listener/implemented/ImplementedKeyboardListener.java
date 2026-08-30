package common.Listener.implemented;

import common.Listener.KeyboardEventListener;
import common.Listener.KeyboardListener;

public class ImplementedKeyboardListener implements KeyboardListener {
	private KeyboardEventListener eventListener;

	public void setListener(KeyboardEventListener eventListener) {
		this.eventListener = eventListener;
	}
}
