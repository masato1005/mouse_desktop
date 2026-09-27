package server.keyboard;

import common.data.KeyboardData;
import common.keyboard.KeyboardManager;
import common.windows.KeyboardWindowsExecutor;
import server.keyboard.listener.ServerKeyboardListener;

public class ServerKeyboardManager extends KeyboardManager {
    private ServerKeyboardListener serverListener;
    private KeyboardWindowsExecutor winExecutor;

    public void setListener(ServerKeyboardListener listener) {
        this.listener = listener;
    }

    public void setRobot(KeyboardWindowsExecutor winExecutor) {
        this.winExecutor = winExecutor;
    }

    public void receiveData(KeyboardData data) {
        System.out.println(1);
        try {
            switch (data.getEventType()) {
                case KEY_DOWN -> winExecutor.pressedKey(data);
                case KEY_UP -> winExecutor.ReleasedKey(data);
                case RELEASE_ALL -> {
                }
            }
        } catch (IllegalStateException e) {
            errorListener.happenError("キー入力でエラーが発生しました");
        }

    }

    @Override
    public void start() {

    }

    @Override
    public void close() {

    }

    @Override
    public void errorHandle() {

    }

}
