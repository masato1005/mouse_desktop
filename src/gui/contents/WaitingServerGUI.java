package gui.contents;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Listener.GUIListener;

public class WaitingServerGUI extends JFrame{
	private GUIListener listener;
	//private JPanel pane;
	//private JLabel label;
	
	public WaitingServerGUI(GUIListener listener) {
		super("待機中");
		this.listener = listener;
		
		JPanel pane  = (JPanel) getContentPane();
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		
		pane.add(Box.createVerticalGlue()); 
		
		JLabel label = new JLabel("サーバー待機中");
		label.setAlignmentX(CENTER_ALIGNMENT);
		pane.add(label);
		
		
		JButton bt = new JButton("停止");
		bt.setAlignmentX(CENTER_ALIGNMENT);
		ActionListener stac = new StopAction();
		bt.addActionListener(stac);
		pane.add(bt);
		
		pane.add(Box.createVerticalGlue()); 
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}
	
	class StopAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("停止");
			dispose();
			listener.pushStop();
		}
		
	}
	
}
