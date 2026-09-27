package common.windows;

import common.data.KeyboardData;

public interface KeyboardWindowsExecutor {
    public void pressedKey(KeyboardData keyboardData);
    public void ReleasedKey(KeyboardData keyboardData);
}
