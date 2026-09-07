package common.gui.listener;

public interface MouseInputListener {
    void moveWheel(int amount);

    void clickLeftMouse(boolean pressed);

    void clickWheelMouse(boolean pressed);

    void clickRightMouse(boolean pressed);
}
