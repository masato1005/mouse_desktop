package platform;

import com.sun.jna.Platform;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser;

public final class WindowsExitHotkey {
    private static final int HOTKEY_ID = 1;
    private static final int MOD_WIN = 0x0008;
    private static final int MOD_NOREPEAT = 0x4000;
    private static final int VK_ESCAPE = 0x1B;
    private static final int WM_HOTKEY = 0x0312;

    private final Runnable exitAction;
    private Thread messageThread;

    public WindowsExitHotkey(Runnable exitAction) {
        this.exitAction = exitAction;
    }

    public synchronized void start() {
        if (!Platform.isWindows() || messageThread != null) {
            return;
        }

        messageThread = new Thread(this::runMessageLoop, "windows-exit-hotkey");
        messageThread.setDaemon(true);
        messageThread.start();
    }

    private void runMessageLoop() {
        User32 user32 = User32.INSTANCE;
        boolean registered = user32.RegisterHotKey(
                null,
                HOTKEY_ID,
                MOD_WIN | MOD_NOREPEAT,
                VK_ESCAPE);

        if (!registered) {
            System.err.println("Win + Esc のグローバルホットキーを登録できませんでした");
            return;
        }

        try {
            WinUser.MSG message = new WinUser.MSG();
            while (user32.GetMessage(message, null, 0, 0) > 0) {
                if (message.message == WM_HOTKEY && message.wParam.intValue() == HOTKEY_ID) {
                    exitAction.run();
                    return;
                }
            }
        } finally {
            user32.UnregisterHotKey(null, HOTKEY_ID);
        }
    }
}
