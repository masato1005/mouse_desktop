package common.keyboard;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.sun.jna.Platform;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.KBDLLHOOKSTRUCT;
import com.sun.jna.platform.win32.WinUser.LowLevelKeyboardProc;

/** Windows全体のキー押下・解放を監視する低レベルキーボードフック。 */
public final class WindowsKeyboardHooker implements AutoCloseable {
    private static final int WM_QUIT = 0x0012;
    private static final int PM_NOREMOVE = 0x0000;
    private static final int LLKHF_INJECTED = 0x00000010;
    private static final long START_TIMEOUT_SECONDS = 5;
    private static final long STOP_TIMEOUT_MILLIS = 3000;

    private volatile HHOOK hookHandle;

    /* GCされないよう、フックを解除するまで強参照を維持する。 */
    private LowLevelKeyboardProc hookCallback;
    private volatile Thread hookThread;
    private volatile int hookThreadId;
    private volatile HookListener listener;
    private volatile boolean localInputSuppressed;
    private final Set<Integer> pressedKeys = new HashSet<>();
    private final Set<Integer> suppressedKeys = new HashSet<>();
    private final Set<Integer> pressedWindowsKeys = new HashSet<>();

    private CountDownLatch startFinished = new CountDownLatch(0);
    private final AtomicReference<RuntimeException> startFailure = new AtomicReference<>();

    public WindowsKeyboardHooker() {
        this(event -> {
        });
    }

    public WindowsKeyboardHooker(HookListener listener) {
        this.listener = Objects.requireNonNull(listener, "listener");
    }

    public void setListener(HookListener listener) {
        this.listener = Objects.requireNonNull(listener, "listener");
    }

    /** 物理キーボードからの入力をローカルPCへ渡すかどうかを切り替える。 */
    public void setLocalInputSuppressed(boolean suppressed) {
        localInputSuppressed = suppressed;
    }

    public boolean isLocalInputSuppressed() {
        return localInputSuppressed;
    }

    /** フック用スレッドを起動し、フックの登録完了まで待機する。 */
    public synchronized void start() {
        if (!Platform.isWindows()) {
            throw new UnsupportedOperationException(
                    "Windows以外ではキーボードフックを利用できません");
        }

        if (hookThread != null && hookThread.isAlive()) {
            return;
        }

        startFailure.set(null);
        startFinished = new CountDownLatch(1);

        hookThread = new Thread(
                this::runMessageLoop,
                "windows-keyboard-hook");

        hookThread.setDaemon(true);
        hookThread.start();

        try {
            boolean started = startFinished.await(
                    START_TIMEOUT_SECONDS,
                    TimeUnit.SECONDS);

            if (!started) {
                close();
                throw new IllegalStateException(
                        "キーボードフックの開始がタイムアウトしました");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            close();
            throw new IllegalStateException(
                    "キーボードフックの開始待機が中断されました",
                    e);
        }

        RuntimeException failure = startFailure.get();
        if (failure != null) {
            hookThread = null;
            throw failure;
        }
    }

    private void runMessageLoop() {
        User32 user32 = User32.INSTANCE;
        hookThreadId = Kernel32.INSTANCE.GetCurrentThreadId();
        hookCallback = this::handleHookEvent;

        try {
            /*
             * PostThreadMessage(WM_QUIT)を確実に受け取れるよう、
             * フック登録前にこのスレッドのメッセージキューを生成する。
             */
            WinUser.MSG message = new WinUser.MSG();
            user32.PeekMessage(message, null, 0, 0, PM_NOREMOVE);

            hookHandle = user32.SetWindowsHookEx(
                    WinUser.WH_KEYBOARD_LL,
                    hookCallback,
                    Kernel32.INSTANCE.GetModuleHandle(null),
                    0);

            if (hookHandle == null) {
                int errorCode = Kernel32.INSTANCE.GetLastError();
                throw new IllegalStateException(
                        "キーボードフックを登録できませんでした。Windows error="
                                + errorCode);
            }

            startFinished.countDown();

            int result;

            while ((result = user32.GetMessage(message, null, 0, 0)) > 0) {
                user32.TranslateMessage(message);
                user32.DispatchMessage(message);
            }

            if (result == -1) {
                throw new IllegalStateException(
                        "キーボードフックのメッセージ取得に失敗しました。Windows error="
                                + Kernel32.INSTANCE.GetLastError());
            }
        } catch (Throwable e) {
            RuntimeException failure = e instanceof RuntimeException runtimeException
                    ? runtimeException
                    : new IllegalStateException("キーボードフックで予期しないエラーが発生しました", e);
            startFailure.compareAndSet(null, failure);
            startFinished.countDown();
        } finally {
            HHOOK currentHandle = hookHandle;
            if (currentHandle != null) {
                user32.UnhookWindowsHookEx(currentHandle);
            }

            hookHandle = null;
            hookCallback = null;
            hookThreadId = 0;
            pressedKeys.clear();
            suppressedKeys.clear();
            pressedWindowsKeys.clear();
            startFinished.countDown();
        }
    }

    private LRESULT handleHookEvent(
            int code,
            WPARAM message,
            KBDLLHOOKSTRUCT keyboardEvent) {

        if (code < 0 || keyboardEvent == null) {
            return callNextHook(code, message, keyboardEvent);
        }

        /* RobotやSendInputなどによる注入入力は再送信しない。 */
        if ((keyboardEvent.flags & LLKHF_INJECTED) != 0) {
            return callNextHook(code, message, keyboardEvent);
        }

        KeyAction action = toKeyAction(message.intValue());
        if (action == null) {
            return callNextHook(code, message, keyboardEvent);
        }

        boolean repeat;
        if (action == KeyAction.DOWN) {
            repeat = !pressedKeys.add(keyboardEvent.vkCode);
            if (isWindowsKey(keyboardEvent.vkCode)) {
                pressedWindowsKeys.add(keyboardEvent.vkCode);
            }
        } else {
            repeat = false;
            pressedKeys.remove(keyboardEvent.vkCode);
        }

        HookEvent event = new HookEvent(
                action,
                toKeyName(keyboardEvent.vkCode),
                keyboardEvent.vkCode,
                keyboardEvent.scanCode,
                repeat);

        try {
            listener.onKeyEvent(event);
        } catch (Throwable ignored) {
            // Windowsのネイティブコールバック境界を越えて例外を出さない。
        }

        boolean blockLocalInput = shouldSuppressLocalInput(
                action,
                keyboardEvent.vkCode,
                repeat);

        if (action == KeyAction.UP && isWindowsKey(keyboardEvent.vkCode)) {
            pressedWindowsKeys.remove(keyboardEvent.vkCode);
        }

        if (blockLocalInput) {
            return new LRESULT(1);
        }

        return callNextHook(code, message, keyboardEvent);
    }

    private boolean shouldSuppressLocalInput(
            KeyAction action,
            int virtualKeyCode,
            boolean repeat) {

        if (action == KeyAction.UP) {
            return suppressedKeys.remove(virtualKeyCode);
        }

        /* KEY_DOWN時の判断を、同じキーのリピートとKEY_UPまで維持する。 */
        if (repeat) {
            return suppressedKeys.contains(virtualKeyCode);
        }

        /* 緊急終了用のWin + Escは常にWindowsへ渡す。 */
        if (isWindowsKey(virtualKeyCode) ||
                virtualKeyCode == 0x1B && !pressedWindowsKeys.isEmpty()) {
            return false;
        }

        if (localInputSuppressed) {
            suppressedKeys.add(virtualKeyCode);
            return true;
        }

        return false;
    }

    private static boolean isWindowsKey(int virtualKeyCode) {
        return virtualKeyCode == 0x5B || virtualKeyCode == 0x5C;
    }

    private LRESULT callNextHook(
            int code,
            WPARAM message,
            KBDLLHOOKSTRUCT keyboardEvent) {

        LPARAM eventPointer = keyboardEvent == null
                ? new LPARAM(0)
                : new LPARAM(com.sun.jna.Pointer.nativeValue(keyboardEvent.getPointer()));

        return User32.INSTANCE.CallNextHookEx(
                hookHandle,
                code,
                message,
                eventPointer);
    }

    private static KeyAction toKeyAction(int message) {
        return switch (message) {
            case WinUser.WM_KEYDOWN, WinUser.WM_SYSKEYDOWN -> KeyAction.DOWN;
            case WinUser.WM_KEYUP, WinUser.WM_SYSKEYUP -> KeyAction.UP;
            default -> null;
        };
    }

    /** WindowsのVirtual-Key値を通信用の安定したキー名へ変換する。 */
    private static String toKeyName(int virtualKeyCode) {
        if (virtualKeyCode >= 0x30 && virtualKeyCode <= 0x39) {
            return Character.toString((char) virtualKeyCode);
        }
        if (virtualKeyCode >= 0x41 && virtualKeyCode <= 0x5A) {
            return Character.toString((char) virtualKeyCode);
        }
        if (virtualKeyCode >= 0x60 && virtualKeyCode <= 0x69) {
            return "NUMPAD_" + (virtualKeyCode - 0x60);
        }
        if (virtualKeyCode >= 0x70 && virtualKeyCode <= 0x87) {
            return "F" + (virtualKeyCode - 0x70 + 1);
        }

        return switch (virtualKeyCode) {
            case 0x08 -> "BACK_SPACE";
            case 0x09 -> "TAB";
            case 0x0D -> "ENTER";
            case 0x10 -> "SHIFT";
            case 0x11 -> "CONTROL";
            case 0x12 -> "ALT";
            case 0x13 -> "PAUSE";
            case 0x14 -> "CAPS_LOCK";
            case 0x1B -> "ESCAPE";
            case 0x20 -> "SPACE";
            case 0x21 -> "PAGE_UP";
            case 0x22 -> "PAGE_DOWN";
            case 0x23 -> "END";
            case 0x24 -> "HOME";
            case 0x25 -> "LEFT";
            case 0x26 -> "UP";
            case 0x27 -> "RIGHT";
            case 0x28 -> "DOWN";
            case 0x2C -> "PRINT_SCREEN";
            case 0x2D -> "INSERT";
            case 0x2E -> "DELETE";
            case 0x5B -> "LEFT_WINDOWS";
            case 0x5C -> "RIGHT_WINDOWS";
            case 0x5D -> "CONTEXT_MENU";
            case 0x6A -> "MULTIPLY";
            case 0x6B -> "ADD";
            case 0x6C -> "SEPARATOR";
            case 0x6D -> "SUBTRACT";
            case 0x6E -> "DECIMAL";
            case 0x6F -> "DIVIDE";
            case 0x90 -> "NUM_LOCK";
            case 0x91 -> "SCROLL_LOCK";
            case 0xA0 -> "LEFT_SHIFT";
            case 0xA1 -> "RIGHT_SHIFT";
            case 0xA2 -> "LEFT_CONTROL";
            case 0xA3 -> "RIGHT_CONTROL";
            case 0xA4 -> "LEFT_ALT";
            case 0xA5 -> "RIGHT_ALT";
            case 0xBA -> "OEM_1";
            case 0xBB -> "OEM_PLUS";
            case 0xBC -> "OEM_COMMA";
            case 0xBD -> "OEM_MINUS";
            case 0xBE -> "OEM_PERIOD";
            case 0xBF -> "OEM_2";
            case 0xC0 -> "OEM_3";
            case 0xDB -> "OEM_4";
            case 0xDC -> "OEM_5";
            case 0xDD -> "OEM_6";
            case 0xDE -> "OEM_7";
            case 0xE2 -> "OEM_102";
            default -> String.format("VK_0x%02X", virtualKeyCode);
        };
    }

    public boolean isRunning() {
        Thread currentThread = hookThread;
        return currentThread != null
                && currentThread.isAlive()
                && hookHandle != null;
    }

    /** メッセージループを終了し、同じスレッドのfinallyでフックを解除する。 */
    @Override
    public synchronized void close() {
        Thread currentThread = hookThread;
        if (currentThread == null) {
            return;
        }

        int currentThreadId = hookThreadId;
        if (currentThreadId != 0) {
            User32.INSTANCE.PostThreadMessage(
                    currentThreadId,
                    WM_QUIT,
                    new WPARAM(0),
                    new LPARAM(0));
        }

        if (currentThread != Thread.currentThread()) {
            try {
                currentThread.join(STOP_TIMEOUT_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        hookThread = null;
    }

    public enum KeyAction {
        DOWN,
        UP
    }

    public record HookEvent(
            KeyAction action,
            String keyName,
            int virtualKeyCode,
            int scanCode,
            boolean repeat) {
    }

    @FunctionalInterface
    public interface HookListener {
        void onKeyEvent(HookEvent event);
    }
}
