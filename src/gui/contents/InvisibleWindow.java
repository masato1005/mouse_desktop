package gui.contents;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class InvisibleWindow extends JFrame {
    private final int width;
    private final int height;

    public InvisibleWindow(int width, int height) {
    super();
    this.width = width;
    this.height = height;

    setUndecorated(true);
    setBackground(new Color(0, 0, 0, 1));
    setAlwaysOnTop(true);
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setSize(width, height);
    setLocation(0, 0);
    invisibleMouse();
    setVisible(false);
}

private void invisibleMouse() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();

    Image image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
    Cursor invisibleCursor = toolkit.createCustomCursor(
            image,
            new Point(0, 0),
            "invisibleCursor");

    setCursor(invisibleCursor);
    getContentPane().setCursor(invisibleCursor);
}

    public void openWindow() {
        setVisible(true);
    }

    public void closeWindow() {
        setVisible(false);
    }

    public static void main(String[] args) {
        InvisibleWindow i = new InvisibleWindow(300, 300);
        i.openWindow();
    }
}
