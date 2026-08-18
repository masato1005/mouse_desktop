package gui.contents;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class ErrorExitGUI extends JFrame {
    private final String massage;

    public ErrorExitGUI(String massage) {
        super("error");
        this.massage = massage;

        JPanel pane = (JPanel) getContentPane();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

        pane.add(Box.createVerticalGlue());

        JLabel label = new JLabel(massage);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        pane.add(label);

        JLabel finishMassage = new JLabel("アプリを終了します");
        finishMassage.setHorizontalAlignment(JLabel.CENTER);
        finishMassage.setAlignmentX(Component.CENTER_ALIGNMENT);
        pane.add(finishMassage);

        pane.add(Box.createVerticalGlue());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(200, 100);
        setLocationRelativeTo(null);
        setVisible(true);

        Timer timer = new Timer(10000, e -> System.exit(0));
        timer.setRepeats(false);
        timer.start();
    }

    public static void main(String[] args) {
        new ErrorExitGUI("test");
    }
}
