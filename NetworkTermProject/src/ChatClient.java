
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * A simple Swing-based client for the chat server. Graphically it is a frame
 * with a text field for entering messages and a textarea to see the whole
 * dialog.
 *
 * The client follows the following Chat Protocol. When the server sends
 * "SUBMITNAME" the client replies with the desired screen name. The server will
 * keep sending "SUBMITNAME" requests as long as the client submits screen names
 * that are already in use. When the server sends a line beginning with
 * "NAMEACCEPTED" the client is now allowed to start sending the server
 * arbitrary strings to be broadcast to all chatters connected to the server.
 * When the server sends a line beginning with "MESSAGE" then all characters
 * following this string should be displayed in its message area.
 */
public class ChatClient {

	String serverAddress;
	Scanner in;
	PrintWriter out;
	JFrame frame = new JFrame("Chatter");
	JTextField textField = new JTextField(50);
	JTextArea messageArea = new JTextArea(16, 50);
	JButton b = new JButton("Whisper");
	String name;

	/**
	 * Constructs the client by laying out the GUI and registering a listener with
	 * the textfield so that pressing Return in the listener sends the textfield
	 * contents to the server. Note however that the textfield is initially NOT
	 * editable, and only becomes editable AFTER the client receives the
	 * NAMEACCEPTED message from the server.
	 */

	public ChatClient(String serverAddress) {
		this.serverAddress = serverAddress;

		textField.setEditable(false);
		messageArea.setEditable(false);

		frame.getContentPane().add(textField, BorderLayout.NORTH);
		frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
		frame.getContentPane().add(b, BorderLayout.SOUTH);
		frame.pack();

		/* whisper ��ư�� �����Ű�� �̸��� ������ <name/>���·� �ٲ��ִ� acctionlistener */
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String whis;
				whis = "<" + getWhisperName() + "/>";
				textField.setText(whis + " ");

			}
		});
		// Send on enter then clear to prepare for next message
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*
				 * server�� ������ �ڽ��� �̸��� ������ ���� ���´��� �˼� �ְ� ^�� name�� ���� ������. (whisper�� ������ ���� �����
				 * ���� �� �� �ֱ⶧���� is not existence�� ����������)
				 */
				out.println(textField.getText() + "^" + name);
				textField.setText("");
			}
		});
	}

	/* ó���� �� �̸��� �����ϴ� UI */
	private String getName() {
		return JOptionPane.showInputDialog(frame, "Choose a screen name:", "Screen name selection",
				JOptionPane.PLAIN_MESSAGE);
	}

	/* whisper�� ���� �̸��� �����ϴ� UI */
	private String getWhisperName() {
		return JOptionPane.showInputDialog(frame, "Whisper name:", "Whisper", JOptionPane.PLAIN_MESSAGE);
	}

	private void run(String ip, int port) throws IOException {

		try {
			Socket socket = new Socket(ip, port);
			in = new Scanner(socket.getInputStream());
			out = new PrintWriter(socket.getOutputStream(), true);

			/* ������ ���� ���� �޴� protocol */
			while (in.hasNextLine()) {
				String line = in.nextLine();
				/* ó���� �������� ���Ӹ޼��� ������ ���ǹ� */
				if (line.startsWith("SUBMITNAME")) {
					name = getName();
					out.println(name);
					/* ó���� �������� ���Ӹ޼��� ������ ���� �̸��� ������ �����ϰ� ���� ���Դ� �� �˷��ִ� ���ǹ� */
				} else if (line.startsWith("NAMEACCEPTED")) {
					this.frame.setTitle("Chatter - " + line.substring(13));
					textField.setEditable(true);
					/* chat message�� ����ϴ� ���ǹ� */
				} else if (line.startsWith("MESSAGE")) {
					messageArea.append(line.substring(8) + "\n");
				}
			}
		} finally {
			frame.setVisible(false);
			frame.dispose();
		}
	}

	public static void main(String[] args) throws Exception {
		Scanner inputStream = null; // txt file���� scanner ����
		String ip = null; // ip�ּ� ������ string ����
		int port = 0; // port������ int ����

		/* txt ���Ͽ��� ip�� port �޴� try and catch */
		try {
			inputStream = new Scanner(new File("serverInfo.txt"));
			while (inputStream.hasNext()) {
				ip = inputStream.next();
				port = inputStream.nextInt();
			}
		} catch (IOException e) { // catch���� ���� txt file���� ���� ���� ��� file�� �ִ� ���� ���� ��������
			System.out.println(e.getMessage());
			ip = "localhost";
			port = 9999;
		}

		ChatClient client = new ChatClient(ip);
		client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.frame.setVisible(true);
		client.run(ip, port);
	}
}
