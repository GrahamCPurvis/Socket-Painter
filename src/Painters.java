import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class Painters extends JFrame implements ActionListener, MouseListener, MouseMotionListener {
	private Color color;
	private Point fPress;
	private p prim = p.CIRCLE;
	private static PaintingPanel PP;
	private JTextArea messageArea;
	private static JTextArea displayArea;
	private JScrollPane displayScroll;
	private static boolean updatePrims;
	private static boolean updateChat;
	private static PaintingPrimitive PaintingPrim;
	private static String chatMessage;
	private static String name;
	private static ObjectInputStream ois;
	private static ObjectOutputStream oos;
	private static boolean updateLocalPrims;
	private static boolean justmade;

	enum p {
		CIRCLE, LINE
	};

	// the painter class splits off into two threads one for reading and one for
	// writing
	// the components of the gui window are accessed and changed through the
	// instance variables
	public static void main(String[] args) throws InterruptedException, ClassNotFoundException {
		try {
			System.out.println("Initiating a connection...");

			Socket s = new Socket("localhost", 6791);
			new Painters();
			System.out.println("Connected");
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			PP = (PaintingPanel) ois.readObject();
			justmade = true;
			JTextArea displayArea2 = (JTextArea) ois.readObject();
			System.out.println(name);
			oos.writeObject(name);
			SockThreadRead Pf = new SockThreadRead(s);
			SockThreadWrite Pf2 = new SockThreadWrite(s);
			Thread th = new Thread(Pf);
			Thread th2 = new Thread(Pf2);
			th.start();
			th2.start();
			th.join();
			th2.join();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// reading thread for painters
	static public class SockThreadRead implements Runnable {
		Socket s;

		public SockThreadRead(Socket s) {
			this.s = s;
		}

		public void run() {
			String str = "I am a string";
			try {
				while (true) {
					// reads in object into a buffer and then either adds it to the text display
					// area
					// or sets a boolean to be true so the primitives will update when any action
					// happens
					Object buff = ois.readObject();
					if (buff.getClass().isInstance(str)) {
						String strBuff = (String) buff;
						displayArea.setText(strBuff);
					} else {
						PaintingPrimitive primBuff = (PaintingPrimitive) buff;
						PP.addPrimitive(primBuff);
						updateLocalPrims = true;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

	}

	static public class SockThreadWrite implements Runnable {
		Socket s;

		public SockThreadWrite(Socket s) {
			this.s = s;
		}

		public void run() {
			try {
				// checks the booleans and writes out chat or primitive if something has changes
				// for this painter
				while (true) {
					Thread.sleep(500);
					if (updatePrims == true) {
						oos.writeObject(PaintingPrim);
						updatePrims = false;
					} else if (updateChat == true) {
						oos.writeObject(chatMessage);
						updateChat = false;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public Painters() {
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
		messageSendButton.setActionCommand("mess");

		messageSendButton.addActionListener(this); // assuming your current class implements ActionListener

		textMessages.add(displayScroll, BorderLayout.SOUTH);
		textMessages.add(messageArea, BorderLayout.CENTER);
		textMessages.add(messageSendButton, BorderLayout.EAST);

		holder.add(textMessages, BorderLayout.SOUTH);

		name = JOptionPane.showInputDialog("Enter your name");

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
		// if mouse is released checks what primitive button was last pressed and paints
		// it as well as
		// sets a boolean that tells the writing thread to tell the hub
		if (this.prim == p.CIRCLE) {
			Circle a = new Circle(fPress, e.getPoint(), this.color);
			PaintingPrim = a;
			// PP.addPrimitive(a);
		} else if (this.prim == p.LINE) {
			Line a = new Line(fPress, e.getPoint(), this.color);
			PaintingPrim = a;
		}
		updatePrims = true;
		if (updateLocalPrims == true || justmade == true) {
			PP.paintComponent(getGraphics());
			justmade = false;
			updateLocalPrims = false;
		}
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
		// if else for action performed that also checks if the local primitives need to
		// be updated
		// and does so regardless of the action
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
			chatMessage = messageArea.getText();
			messageArea.setText("");
			updateChat = true;
		} else {
			System.out.println("Something went Wrong");
		}
		if (updateLocalPrims == true || justmade == true) {
			PP.paintComponent(getGraphics());
			justmade = false;
			updateLocalPrims = false;
		}

	}

}
