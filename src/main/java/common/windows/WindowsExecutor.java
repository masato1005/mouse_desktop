package common.windows;

import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.WinUser.INPUT;
import com.sun.jna.platform.win32.WinUser.KEYBDINPUT;

import common.data.KeyboardData;
import common.main.handler.ErrorHandler;

public class WindowsExecutor implements KeyboardWindowsExecutor {
    private final ErrorHandler errorHandler;

    public WindowsExecutor(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    private void sendScanCode(int scanCode, boolean extended, boolean keyUp) {
        INPUT[] inputs = (INPUT[]) new INPUT().toArray(1);
        INPUT input = inputs[0];

        input.type = new DWORD(INPUT.INPUT_KEYBOARD);

        KEYBDINPUT keyboard = new KEYBDINPUT();
        keyboard.wVk = new WORD(0);
        keyboard.wScan = new WORD(scanCode);

        int flags = KEYBDINPUT.KEYEVENTF_SCANCODE;

        if (extended) {
            flags |= KEYBDINPUT.KEYEVENTF_EXTENDEDKEY;
        }

        if (keyUp) {
            flags |= KEYBDINPUT.KEYEVENTF_KEYUP;
        }

        keyboard.time = new DWORD(0);
        keyboard.dwExtraInfo = new ULONG_PTR(KeyboardInjectionMarker.VALUE);

        input.input.setType(KEYBDINPUT.class);
        input.input.ki = keyboard;
        input.write();

        DWORD sent = User32.INSTANCE.SendInput(
                new DWORD(inputs.length),
                inputs,
                input.size());

        if (sent.intValue() != inputs.length) {
            int error = Kernel32.INSTANCE.GetLastError();
            throw new IllegalStateException(
                    "SendInput failed: " + error);
        }
    }

    @Override
    public void pressedKey(KeyboardData keyboardData)throws IllegalStateException {
        
        sendScanCode(keyboardData.getScanCode(), keyboardData.isExtendedKey(), false);
    }

    @Override
    public void ReleasedKey(KeyboardData keyboardData) throws IllegalStateException{
        sendScanCode(keyboardData.getScanCode(), keyboardData.isExtendedKey(), true);
    }

}
