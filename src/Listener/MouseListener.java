package Listener;

import Json.MouseData;

public interface MouseListener {
	void mouseLeftClicked();

	void mouseMoved(MouseData mousedata);

	void mouseRightClicked();

	void mouseDragged();

	void mouseWheelClicked();

	void mouseWheelMoved();

    void openInvisibleWindow();

    void closeInvisibleWindow();
}
