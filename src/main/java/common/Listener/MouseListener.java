package common.Listener;

import common.data.MouseData;

public interface MouseListener {
	void mouseMoved(MouseData mouseData);

	void mouseLeftClicked(MouseData mouseData);

	void mouseRightClicked(MouseData mouseData);

	void mouseWheelClicked(MouseData mouseData);

	void mouseWheelMoved(MouseData mouseData);

	void mouseDragged();

    void openInvisibleWindow();

	void closeInvisibleWindow();
}
