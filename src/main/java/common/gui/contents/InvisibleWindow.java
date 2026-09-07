package common.gui.contents;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import common.gui.listener.MouseGuiListener;

public class InvisibleWindow extends JFrame {

    private final int width;
    private final int height;
    private final MouseGuiListener listener;

    public InvisibleWindow(int width, int height, MouseGuiListener listener) {
        super();
        this.width = width;
        this.height = height;
        this.listener = listener;
        System.out.println(3);

        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 1));
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(width, height);
        setLocation(0, 0);
        invisibleMouse();
        addMouseWheelListener(new MouseWheelListener());
        addMouseListener(new MyMouseListener());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });
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

    private void confirmExit() {
        String msg = "終了しますか？";
        int ans = JOptionPane.showConfirmDialog(this, msg);
        if (ans == JOptionPane.YES_OPTION) {
            listener.systemExit();
            System.exit(0);
        }
    }

    public void openWindow() {
        setVisible(true);
        System.out.println(4);
    }

    public void closeWindow() {
        setVisible(false);
    }

    class MouseWheelListener implements java.awt.event.MouseWheelListener {

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            int amount = e.getWheelRotation();
            listener.moveWheel(amount);
        }
    }

    class MyMouseListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            switch (e.getButton()) {
                case MouseEvent.BUTTON1 -> {
                    listener.clickLeftMouse(true);
                }
                case MouseEvent.BUTTON2 -> {
                    listener.clickWheelMouse(true);
                }
                case MouseEvent.BUTTON3 -> {
                    listener.clickRightMouse(true);
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            switch (e.getButton()) {
                case MouseEvent.BUTTON1 -> {
                    listener.clickLeftMouse(false);
                }
                case MouseEvent.BUTTON2 -> {
                    listener.clickWheelMouse(false);
                }
                case MouseEvent.BUTTON3 -> {
                    listener.clickRightMouse(false);
                }
            }
        }
    }

}
