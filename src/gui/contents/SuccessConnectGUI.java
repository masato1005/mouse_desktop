package gui.contents;

import Listener.GUIListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SuccessConnectGUI extends JFrame{
	private GUIListener listener;

	public SuccessConnectGUI(GUIListener listener) {
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

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}
	
	class OkAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
			listener.pushedSuccessOkButton();
		}

	}
}
