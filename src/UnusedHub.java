import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

//Do not use this for testing
public class UnusedHub {
	private Color color;
	private Point fPress;
	private p prim;
	private static PaintingPanel PP = new PaintingPanel();
	private static JTextArea messageArea;
	private static JTextArea displayArea;
	private static JScrollPane displayScroll;
	private static boolean updatePrims;
	private static boolean updateChat;
	private static PaintingPrimitive PaintingPrim;
	private static String chatMessage;
	private static ObjectInputStream[] ois = new ObjectInputStream[100];
	private static ObjectOutputStream[] oos = new ObjectOutputStream[100];
	private static int numThreads = 0;

	enum p {
		CIRCLE, LINE
	};

	public static void main(String[] args) {
		int numSockets = 2;
		try {
			displayArea = new JTextArea(5, 20);
			messageArea = new JTextArea(1, 20);
			displayArea.setLineWrap(true);
			displayArea.setWrapStyleWord(true);
			displayScroll = new JScrollPane(displayArea);
			PP = new PaintingPanel();

			ServerSocket ss = new ServerSocket(6791);
			System.out.println("Waiting for connection");
			Socket s[] = new Socket[numSockets];
			String[] names = new String[numSockets];
			Thread[] threads = new Thread[numSockets];
			Thread[] threads2 = new Thread[numSockets];
			for (int i = 0; i < numSockets; i++) {
				s[i] = ss.accept();
				numThreads++;
			}
			System.out.println("Accepted");
			int numSock = 0;
			for (int i = 0; i < numSockets; i++) {
				SockThreadRead Pf = new SockThreadRead(PP, displayArea, i, s[i], names);
				SockThreadWrite Pf2 = new SockThreadWrite(PP, displayArea, i, s[i], names);
				ois[i] = new ObjectInputStream(s[i].getInputStream());
				oos[i] = new ObjectOutputStream(s[i].getOutputStream());
				numSock++;
				Thread th = new Thread(Pf);
				threads[i] = th;
				Thread th2 = new Thread(Pf2);
				threads2[i] = th2;
				th.start();
				th2.start();
			}
			for (int i = 0; i < numSockets; i++) {
				try {
					threads[i].join();
					threads2[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	static public class SockThreadRead implements Runnable {
		private PaintingPanel PP2;
		private JTextArea messageArea;
		private JTextArea displayArea2;
		private JScrollPane displayScroll;
		private String[] listNames;
		private int threadNo;
		Socket s;

		public SockThreadRead(PaintingPanel PP2, JTextArea dA, int tno, Socket s, String[] nams) {
			this.PP2 = PP2;
			this.displayArea2 = dA;
			this.threadNo = tno;
			this.listNames = nams;
			this.s = s;
		}

		public void run() {
			String str = "I am a string";
			try {
				oos[threadNo].writeObject(PP2);
				oos[threadNo].writeObject(displayArea2);
				listNames[threadNo] = (String) ois[threadNo].readObject();
				System.out.println(listNames[threadNo] + " apples");
				while (true) {
					Object buff = ois[threadNo].readObject();
					if (buff == null) {
						String strBuff = (String) buff;
						chatMessage = listNames[threadNo] + ": " + strBuff + "\n" + displayArea.getText();
						displayArea.setText(chatMessage);
						updateChat = true;
					} else if (buff.getClass().isInstance(str)) {
						String strBuff = (String) buff;
						chatMessage = listNames[threadNo] + ": " + strBuff + "\n" + displayArea.getText();
						displayArea.setText(chatMessage);
						updateChat = true;
					} else {
						PaintingPrimitive primBuff = (PaintingPrimitive) buff;
						PP.addPrimitive(primBuff);
						PaintingPrim = primBuff;
						updatePrims = true;
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
		private PaintingPanel PP2;
		private JTextArea messageArea;
		private JTextArea displayArea2;
		private JScrollPane displayScroll;
		private String[] listNames;
		private int threadNo;
		Socket s;

		public SockThreadWrite(PaintingPanel PP2, JTextArea dA, int tno, Socket s, String[] nams) {
			this.PP2 = PP2;
			this.displayArea2 = dA;
			this.threadNo = tno;
			this.listNames = nams;
			this.s = s;
		}

		public void run() {
			try {
				while (true) {
					Thread.sleep(500);
					if (updatePrims == true) {
						for (int i = 0; i < numThreads; i++) {
							oos[i].writeObject(PaintingPrim);
						}
						updatePrims = false;
					} else if (updateChat == true) {
						for (int i = 0; i < numThreads; i++) {
							oos[i].writeObject(chatMessage);
						}
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

}
