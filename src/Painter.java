import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class Painter extends JFrame implements ActionListener, MouseListener, MouseMotionListener {
	private Color color;
	private Point fPress;
	private p prim;
	private PaintingPanel PP;
	private JTextArea messageArea;
	private JTextArea displayArea;
	private JScrollPane displayScroll;

	enum p {
		CIRCLE, LINE
	};

	public static void main(String[] args) {
		new Painter();
	}

	public Painter() {
		setSize(500, 500);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		JPanel holder = new JPanel();
		holder.setLayout(new BorderLayout());

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new GridLayout(3, 1));

		// red button
		JButton redPaint = new JButton();
		redPaint.setBackground(Color.RED);
		redPaint.setOpaque(true);
		redPaint.setBorderPainted(false);
		leftPanel.add(redPaint);
		redPaint.addActionListener(this);
		redPaint.setActionCommand("r");

		// green button
		JButton greenPaint = new JButton();
		greenPaint.setBackground(Color.GREEN);
		greenPaint.setOpaque(true);
		greenPaint.setBorderPainted(false);
		leftPanel.add(greenPaint);
		greenPaint.addActionListener(this);
		greenPaint.setActionCommand("g");
		// blue button
		JButton bluePaint = new JButton();
		bluePaint.setBackground(Color.BLUE);
		bluePaint.setOpaque(true);
		bluePaint.setBorderPainted(false);
		leftPanel.add(bluePaint);

		bluePaint.addActionListener(this);
		bluePaint.setActionCommand("b");

		holder.add(leftPanel, BorderLayout.WEST);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(1, 2));
		JButton circle = new JButton("Circle");

		circle.addActionListener(this);
		circle.setActionCommand("c");

		topPanel.add(circle);

		JButton line = new JButton("Line");
		line.addActionListener(this);

		line.setActionCommand("l");

		topPanel.add(line);

		holder.add(topPanel, BorderLayout.NORTH);

		// Where to add painter panel
		PP = new PaintingPanel();
		holder.add(PP, BorderLayout.CENTER);
		PP.addMouseListener(this);
		// System.out.println("here");

		// Keep track of the text area to write to it later
		// (these should probably be class instance variables)

		// Build the text area
		JPanel textMessages = new JPanel();
		textMessages.setLayout(new BorderLayout());

		displayArea = new JTextArea(5, 20);
		messageArea = new JTextArea(1, 20);
		displayArea.setLineWrap(true);
		displayArea.setWrapStyleWord(true);
		displayScroll = new JScrollPane(displayArea);
		JButton messageSendButton = new JButton();

		messageSendButton.setText("Send Message");
		messageSendButton.setActionCommand("mess"); // the ActionEvent passed into actionPerformed()
													// has a .getActionCommand() method that returns
													// the string you choose here (in this case, "message").
													// You can use this action command string to handle
													// multiple actions in the same actionPerformed() method
													//
													// eg:
													// void actionPerformed(ActionEvent e) {
													// if(e.getActionCommand().equals("message")) {
													// // handle message logic
													// } else if(e.getActionCommand().equals("circle") {
													// // handle circle drawing logic
													// } etc...
													// }

		messageSendButton.addActionListener(this); // assuming your current class implements ActionListener

		textMessages.add(displayScroll, BorderLayout.SOUTH);
		textMessages.add(messageArea, BorderLayout.CENTER);
		textMessages.add(messageSendButton, BorderLayout.EAST);

		holder.add(textMessages, BorderLayout.SOUTH);
		String name = JOptionPane.showInputDialog("Enter your name");

		setContentPane(holder);
		setVisible(true);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		fPress = e.getPoint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// PaintingPanel
		if (this.prim == p.CIRCLE) {
			Circle a = new Circle(fPress, e.getPoint(), this.color);
			PP.addPrimitive(a);
		} else if (this.prim == p.LINE) {
			Line a = new Line(fPress, e.getPoint(), this.color);
			PP.addPrimitive(a);
		}
		System.out.println("here");
		PP.paintComponent(getGraphics());
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String in = e.getActionCommand();
		// System.out.println("here");
		if (in.equals("r")) {
			this.color = Color.RED;
		} else if (in.equals("g")) {
			this.color = Color.GREEN;
		} else if (in.equals("b")) {
			this.color = Color.BLUE;
		} else if (in.equals("c")) {
			this.prim = p.CIRCLE;
		} else if (in.equals("l")) {
			this.prim = p.LINE;
		} else if (in.equals("mess")) {
			String m = messageArea.getText();
			displayArea.setText(m + "\n" + displayArea.getText());
			messageArea.setText("");
		} else {
			System.out.println("Something went Wrong");
		}

	}

}
