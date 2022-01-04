import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Hub {
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
		int numSockets = 100;
		try {
			// setting up the display and painting panel area to keep track of and give to
			// other painters
			displayArea = new JTextArea(5, 20);
			messageArea = new JTextArea(1, 20);
			displayArea.setLineWrap(true);
			displayArea.setWrapStyleWord(true);
			displayScroll = new JScrollPane(displayArea);
			PP = new PaintingPanel();

			// setting up server socket
			ServerSocket ss = new ServerSocket(6791);
			System.out.println("Waiting for connection");
			Socket s[] = new Socket[numSockets];
			String[] names = new String[numSockets];
			Thread[] threads = new Thread[numSockets];
			Thread[] threads2 = new Thread[numSockets];
			// this for loop goes up to 100 players and adds two new threads for each
			// painter
			// on thread takes in writing and one thread reads (implemented down below)

			for (int i = 0; i < numSockets; i++) {
				s[i] = ss.accept();
				System.out.println("Accepted");
				SockThreadRead Pf = new SockThreadRead(PP, displayArea, i, s[i], names);
				SockThreadWrite Pf2 = new SockThreadWrite(PP, displayArea, i, s[i], names);
				ois[i] = new ObjectInputStream(s[i].getInputStream());
				oos[i] = new ObjectOutputStream(s[i].getOutputStream());
				Thread th = new Thread(Pf);
				threads[i] = th;
				Thread th2 = new Thread(Pf2);
				threads2[i] = th2;
				th.start();
				th2.start();
				numThreads++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// reading thread
	static public class SockThreadRead implements Runnable {
		private PaintingPanel PP2;
		private JTextArea displayArea2;
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
				while (true) {
					// checks type of object
					Object buff = ois[threadNo].readObject();
					// if buff is null enters empty chat in display area
					if (buff == null) {
						String strBuff = "";
						chatMessage = listNames[threadNo] + ": " + strBuff + "\n" + displayArea.getText();
						displayArea.setText(chatMessage);
						updateChat = true;

						// if buff is string it takes in the name of the painter and adds it to the
						// display area
						// it also sets a boolean to true so the write thread knows to write to the
						// other sockets
					} else if (buff.getClass().isInstance(str)) {
						String strBuff = (String) buff;
						chatMessage = listNames[threadNo] + ": " + strBuff + "\n" + displayArea.getText();
						displayArea.setText(chatMessage);
						updateChat = true;

						// otherwise a primitive is passed and therefore it is added to the hubs
						// background memory
						// painting panel and also a boolean is set to tell the writing thread
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
					// if primitives need to be updated it is written to all painters
					if (updatePrims == true) {
						for (int i = 0; i < numThreads; i++) {
							oos[i].writeObject(PaintingPrim);
						}
						updatePrims = false;
						// if update chat is true then the new display area is written to all painters
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
