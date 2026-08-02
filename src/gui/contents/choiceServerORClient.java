package gui.contents;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JOptionPane;

import Listener.GUIListener;

public class choiceServerORClient extends JFrame {
	private int option = -1;
	private GUIListener guiListener;

	JPanel pane;

	public choiceServerORClient(GUIListener guiListener) {
		super("choiceServerORClient");
		this.guiListener = guiListener;
		// メインパネル
		pane = (JPanel) getContentPane();
		pane.setLayout(new BorderLayout());

		ActionListener dicisionListener = new dicisionAction();
		ActionListener RadioListener = new RadioAction();

		pane.add(new JLabel("マウスやキーボードが接続されているパソコンはクライアントを選択してください。"), BorderLayout.NORTH);

		// ラジオボタン
		JPanel centerPane = new JPanel();
		centerPane.setLayout(new BoxLayout(centerPane, BoxLayout.Y_AXIS));
		centerPane.add(Box.createVerticalGlue());
		pane.add(centerPane, BorderLayout.CENTER);
		ButtonGroup buttonGrope = new ButtonGroup();
		JRadioButton serverButton = new JRadioButton("サーバー");
		centerPane.add(serverButton);
		buttonGrope.add(serverButton);
		serverButton.addActionListener(RadioListener);
		JRadioButton clientButton = new JRadioButton("クライアント");
		centerPane.add(clientButton);
		buttonGrope.add(clientButton);
		clientButton.addActionListener(RadioListener);
		centerPane.add(Box.createVerticalGlue());

		// 決定ボタン
		JPanel southPane = new JPanel();
		pane.add(southPane, BorderLayout.SOUTH);
		southPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton decisionButton = new JButton("決定");
		decisionButton.addActionListener(dicisionListener);
		southPane.add(decisionButton);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		pack();
		setVisible(true);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				confirmExit();
			}
		});
	}


	private void confirmExit(){
		String msg = "終了しますか？";
		int ans = JOptionPane.showConfirmDialog(pane, msg);
		if(ans == 0){
			guiListener.systemExit();
			System.exit(0);
		}

	}

	class dicisionAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (option != -1) {
				try {
					dispose();
					if (option == 1)
						guiListener.choiseServer();
					if (option == 0)
						guiListener.choiseClient();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	class RadioAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd.equals("サーバー")) {
				option = 1;
			} else if (cmd.equals("クライアント")) {
				option = 0;
			}
		}
	}

}
