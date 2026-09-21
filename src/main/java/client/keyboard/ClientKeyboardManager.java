package client.keyboard;

import common.keyboard.KeyboardManager;
import common.keyboard.WindowsKeyboardHooker;

public class ClientKeyboardManager extends KeyboardManager {
    protected WindowsKeyboardHooker hooker;

    @Override
    public void start() {
        hookerStart();
    }

    private void hookerStart() {
        if (hooker == null)
            hooker = new WindowsKeyboardHooker();
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
        if(hooker == null)
            return;
        hooker.setLocalInputSuppressed(false);
        hooker.close();
    }

    @Override
    public void errorHandle() {
        close();
    }

}
