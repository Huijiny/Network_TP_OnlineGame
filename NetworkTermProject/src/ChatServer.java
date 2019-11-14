

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A multithreaded chat room server. When a client connects the server requests
 * a screen name by sending the client the text "SUBMITNAME", and keeps
 * requesting a name until a unique one is received. After a client submits a
 * unique name, the server acknowledges with "NAMEACCEPTED". Then all messages
 * from that client will be broadcast to all other clients that have submitted a
 * unique screen name. The broadcast messages are prefixed with "MESSAGE".
 *
 * This is just a teaching example so it can be enhanced in many ways, e.g.,
 * better logging. Another is to accept a lot of fun commands, like Slack.
 */
public class ChatServer {

	// All client names, so we can check for duplicates upon registration.
	private static ArrayList<String> names = new ArrayList<String>();

	// The set of all the print writers for all the clients, used for broadcast.
	private static ArrayList<PrintWriter> writers = new ArrayList<PrintWriter>();

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

		System.out.println("The chat server is running...");
		ExecutorService pool = Executors.newFixedThreadPool(500); // multithread ����
		try (ServerSocket listener = new ServerSocket(port)) {
			while (true) {
				pool.execute(new Handler(listener.accept())); // mutithread ����
			}
		}
	}

	/**
	 * The client handler task.
	 */
	private static class Handler implements Runnable {
		private String name;
		private Socket socket;
		private Scanner in;
		private PrintWriter out;

		/**
		 * Constructs a handler thread, squirreling away the socket. All the interesting
		 * work is done in the run method. Remember the constructor is called from the
		 * server's main method, so this has to be as short as possible.
		 */
		public Handler(Socket socket) {
			this.socket = socket;
		}

		/**
		 * Services this thread's client by repeatedly requesting a screen name until a
		 * unique one has been submitted, then acknowledges the name and registers the
		 * output stream for the client in a global set, then repeatedly gets inputs and
		 * broadcasts them.
		 */
		public void run() {

			try {
				in = new Scanner(socket.getInputStream());
				out = new PrintWriter(socket.getOutputStream(), true);

				// Keep requesting a name until we get a unique one.
				while (true) {
					out.println("SUBMITNAME");
					name = in.nextLine();
					if (name == null) {
						return;
					}
					synchronized (names) {
						if (name.length() > 0 && !names.contains(name)) {
							names.add(name);
							break;
						}
					}
				}

				// Now that a successful name has been chosen, add the socket's print writer
				// to the set of all writers so this client can receive broadcast messages.
				// But BEFORE THAT, let everyone else know that the new person has joined!
				out.println("NAMEACCEPTED " + name);
				for (PrintWriter writer : writers) {
					writer.println("MESSAGE " + name + " has joined");
				}
				writers.add(out);

				// Accept messages from this client and broadcast them.
				while (true) {
					String input = in.nextLine();
					if (input.toLowerCase().startsWith("/quit")) {
						return;
					}
					/* whisper ���� üũ�ϴ� if�� */
					else if (input.startsWith("<")) {
						int i = 1;
						String n = "";
						String l = "";
						String key = "";
						int check1 = 0;
						int check2 = 0;
						/* '/'���� �о �̸��� ��� �ݺ��� */
						while (!(input.charAt(i) == '/' && input.charAt(i + 1) == '>')) {
							if (input.length() - 1 == i) {
								check2++;
								System.out.println("1");
								i = 0;
								break;
							}
							n += input.charAt(i);
							i++;

						}
						i += 2;

						System.out.println("i " + i + "\ni's lenght" + input.length());

						if (check2 != 1) {
							System.out.println("i " + i + "\ni's lenght" + input.length());
							/* �޼��� �ؽ�Ʈ�� �޴� �ݺ��� */
							while (input.charAt(i) != '^') {

								l += input.charAt(i);
								i++;

							}
							i++;
							/* ���� ����� ������ �ޱ� ���� �ݺ��� */
							while (input.length() != i) {
								key += input.charAt(i);
								i++;
							}

						}
						/* ������ ���� �ִ� ������� Ȯ���ϰ� ������ �׻������ whisper�� ������ �ݺ��� */
						for (int k = 0; k < writers.size(); k++) {
							if (n.equals(names.get(k))) {
								System.out.println(name);
								writers.get(k).println("MESSAGE" + " /W " + name + ": " + l);
								check1 = 1;
							}
						}
						/* whisper�� ������ Ʋ���� �� */
						if (check2 == 1) {
							for (PrintWriter writer : writers) {
								writer.println("MESSAGE " + name + ": "
										+ input.substring(0, input.length() - name.length() - 1));
							}
						}
						/* ���� �� ������ �� ����� �������� ������ ���� ������� �������� �ʴ´ٰ� ������ �ݺ��� */
						if (check2 != 1 && check1 == 0) {
							for (int k = 0; k < writers.size(); k++) {
								if (key.equals(names.get(k))) {
									System.out.println(name);
									writers.get(k).println("MESSAGE" + " " + n + " is not existence");

								}
							}
						}

					}
					/* whisper�� �ƴҶ� */
					else {
						for (PrintWriter writer : writers) {
							writer.println(
									"MESSAGE " + name + ": " + input.substring(0, input.length() - name.length() - 1));
						}
					}

				}
			} catch (Exception e) {
				System.out.println(e);
			} finally {
				if (out != null) {
					writers.remove(out);
				}
				if (name != null) {
					System.out.println(name + " is leaving");
					names.remove(name);
					for (PrintWriter writer : writers) {
						writer.println("MESSAGE " + name + " has left");
					}
				}
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
