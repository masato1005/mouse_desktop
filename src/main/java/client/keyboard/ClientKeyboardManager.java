package client.keyboard;

import client.keyboard.listener.ClientKeyboardListener;
import client.keyboard.listener.hookerListener;
import common.data.KeyboardData;
import common.eventType.DataType;
import common.eventType.KeyboardEventType;
import common.keyboard.KeyboardManager;
import common.keyboard.WindowsKeyboardHooker;

public class ClientKeyboardManager extends KeyboardManager implements hookerListener {
    protected WindowsKeyboardHooker hooker;
    private ClientKeyboardListener clientListener;

    public void setListener(ClientKeyboardListener clientListener) {
        this.clientListener = clientListener;
    }

    @Override
    public void start() {
        hookerStart();
    }

    private void hookerStart() {
        if (hooker == null)
            hooker = new WindowsKeyboardHooker(this::onEvent);
        hooker.start();
    }

    public void changeCursorOwner(boolean haveMouse) {
        hooker.setLocalInputSuppressed(!haveMouse);
    }

    @Override
    public void close() {
        hookerClose();
    }

    private void hookerClose() {
        if (hooker == null)
            return;
        hooker.setLocalInputSuppressed(false);
        hooker.close();
    }

    @Override
    public void errorHandle() {
        close();
    }

    @Override
    public void onEvent(WindowsKeyboardHooker.HookEvent event) {
        KeyboardEventType eventType;
        switch (event.action()) {
            case DOWN -> {
                eventType = KeyboardEventType.KEY_DOWN;
                KeyboardData data = new KeyboardData(eventType, event.keyName(), event.virtualKeyCode(),
                        event.scanCode(), event.repeat());
                DataType dataType = DataType.KEYBOARD;
                clientListener.happenKeyboardEvent(dataType, data);
            }
            case UP -> {
                eventType = KeyboardEventType.KEY_UP;
                KeyboardData data = new KeyboardData(eventType, event.keyName(), event.virtualKeyCode(),
                        event.scanCode(), event.repeat());
                DataType dataType = DataType.KEYBOARD;
                clientListener.happenKeyboardEvent(dataType, data);
            }
        }

    }

}
