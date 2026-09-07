package common.mouse.listener;

import common.data.MouseData;

public interface MouseButtonListener {
    void mouseLeftClicked(MouseData mouseData);
    void mouseRightClicked(MouseData mouseData);
    void mouseWheelClicked(MouseData mouseData);
    void mouseDragged();
}
