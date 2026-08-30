package gui.contents;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Listener.GuiListener;

public class TimeoutGui extends JFrame {
	private final GuiListener listener;

	public TimeoutGui(GuiListener listener) {
		super("タイムアウト");
		this.listener = listener;

		JPanel pane = (JPanel) getContentPane();
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

		pane.add(Box.createVerticalGlue());

		JLabel label1 = new JLabel("タイムアウトしました。");
		label1.setAlignmentX(CENTER_ALIGNMENT);
		pane.add(label1);

		JLabel label2 = new JLabel("再試行しますか？");
		label2.setAlignmentX(CENTER_ALIGNMENT);
		pane.add(label2);

		JButton bt = new JButton("再試行");
		bt.setAlignmentX(CENTER_ALIGNMENT);
		ActionListener stac = new RetryAction();
		bt.addActionListener(stac);
		pane.add(bt);

		pane.add(Box.createVerticalGlue());

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		pack();
		setVisible(true);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				confirmExit();
			}
		});
	}

	private void confirmExit() {
		String msg = "終了しますか？";
		int ans = JOptionPane.showConfirmDialog(this, msg);
		if (ans == JOptionPane.YES_OPTION) {
			listener.systemExit();
			System.exit(0);
		}
	}

	class RetryAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("再試行");
			dispose();
			listener.retry();
		}

	}

}
