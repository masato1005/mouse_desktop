package Listener;

import data.MouseData;

public interface MouseListener {
	void mouseLeftClicked(MouseData mouseData);

	void mouseMoved(MouseData mouseData);

	void mouseRightClicked(MouseData mouseData);

	void mouseDragged();

	void mouseWheelClicked(MouseData mouseData);

	void mouseWheelMoved(MouseData mouseData);

    void openInvisibleWindow();

	void closeInvisibleWindow();
}
