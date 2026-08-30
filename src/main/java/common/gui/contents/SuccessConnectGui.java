package common.gui.contents;

import common.Listener.GuiListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class SuccessConnectGui extends JFrame{
	private final GuiListener listener;

	public SuccessConnectGui(GuiListener listener) {
		super("Success");
		this.listener = listener;

		JPanel pane = (JPanel) getContentPane();
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		JLabel label = new JLabel("接続が完了しました。");
		label.setAlignmentX(CENTER_ALIGNMENT);
		pane.add(label);

		JButton bt = new JButton("OK");
		bt.addActionListener(new OkAction());
		bt.setAlignmentX(CENTER_ALIGNMENT);
		pane.add(bt);

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
	
	class OkAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
			listener.pushedSuccessOkButton();
		}

	}
}
