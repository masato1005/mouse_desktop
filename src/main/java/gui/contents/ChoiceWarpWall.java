package gui.contents;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Listener.GUIListener;

public class ChoiceWarpWall extends JFrame {
	private final GUIListener listener;

	public ChoiceWarpWall(GUIListener listener) {
		super();
		this.listener = listener;
		JPanel mainPane = (JPanel) getContentPane();

		JPanel leftPane = new JPanel(new BorderLayout(0, 0));
		leftPane.setSize(16, 9);
		leftPane.add(new RectDrawer(true), BorderLayout.CENTER);
		RectDrawer rect = new RectDrawer(false);
		leftPane.add(rect, BorderLayout.WEST);

		JPanel rightPane = new JPanel();
		rightPane.setLayout(new BoxLayout(rightPane, BoxLayout.Y_AXIS));
		rightPane.add(new JLabel("接続するモニターの位置を決めてください。"));

		String[] location = { "上", "下", "右", "左" };
		JComboBox<String> combo = new JComboBox<String>(location);
		combo.addItemListener(new ItemHandler(leftPane, this, rect));
		combo.setSelectedIndex(3);
		rightPane.add(combo);

		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.X_AXIS));
		mainPane.add(leftPane);
		mainPane.add(rightPane);

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

	class ItemHandler implements ItemListener {
		final JPanel leftPane;
		final ChoiceWarpWall wall;
		RectDrawer rect;

		public ItemHandler(JPanel leftPane, ChoiceWarpWall wall, RectDrawer rect) {
			this.leftPane = leftPane;
			this.wall = wall;
			this.rect = rect;
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() != ItemEvent.SELECTED)
				return;
			String location = e.getItem().toString();
			leftPane.remove(rect);
			rect = new RectDrawer(false);
			switch (location) {
			case "上":
				leftPane.add(rect, BorderLayout.NORTH);
				listener.choiceNorthWall();
				break;
			case "下":
				leftPane.add(rect, BorderLayout.SOUTH);
				listener.choiceSouthWall();
				break;
			case "右":
				leftPane.add(rect, BorderLayout.EAST);
				listener.choiceEastWall();
				break;
			case "左":
				leftPane.add(rect, BorderLayout.WEST);
				listener.choiceWestWall();
				break;
			}
			wall.pack();
			leftPane.revalidate();
			leftPane.repaint();
		}
	}

	class RectDrawer extends JPanel {
		private final boolean currentPC;

		public RectDrawer(boolean currentPC) {
			this.currentPC = currentPC;
			setPreferredSize(new Dimension(100, 50));
			setOpaque(false);
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			if (currentPC) {
				g.setColor(Color.WHITE);
			} else {
				g.setColor(Color.CYAN);
			}
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, getWidth(), getHeight());

			String text = currentPC ? "自PC" : "外部";

			int stringWidth = g.getFontMetrics().stringWidth(text);
			int stringAscent = g.getFontMetrics().getAscent();

			int x = (getWidth() - stringWidth) / 2;
			int y = (getHeight() / 2) + (stringAscent / 2) - 2;

			g.drawString(text, x, y);
		}
	}
}
