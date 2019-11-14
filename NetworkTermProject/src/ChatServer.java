

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
		Scanner inputStream = null; // txt file받을 scanner 생성
		String ip = null; // ip주소 저장할 string 생성
		int port = 0; // port저장할 int 생성

		/* txt 파일에서 ip와 port 받는 try and catch */
		try {
			inputStream = new Scanner(new File("serverInfo.txt"));
			while (inputStream.hasNext()) {
				ip = inputStream.next();
				port = inputStream.nextInt();
			}
		} catch (IOException e) { // catch에서 만약 txt file에서 받지 못할 경우 file에 있는 같은 값을 지정해줌
			System.out.println(e.getMessage());
			ip = "localhost";
			port = 9999;
		}

		System.out.println("The chat server is running...");
		ExecutorService pool = Executors.newFixedThreadPool(500); // multithread 생성
		try (ServerSocket listener = new ServerSocket(port)) {
			while (true) {
				pool.execute(new Handler(listener.accept())); // mutithread 실행
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
					/* whisper 인지 체크하는 if문 */
					else if (input.startsWith("<")) {
						int i = 1;
						String n = "";
						String l = "";
						String key = "";
						int check1 = 0;
						int check2 = 0;
						/* '/'까지 읽어서 이름을 얻는 반복문 */
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
							/* 메세지 텍스트를 받는 반복문 */
							while (input.charAt(i) != '^') {

								l += input.charAt(i);
								i++;

							}
							i++;
							/* 보낸 사람이 누군지 받기 위한 반복문 */
							while (input.length() != i) {
								key += input.charAt(i);
								i++;
							}

						}
						/* 서버에 들어와 있는 사람인지 확인하고 있으면 그사람에게 whisper를 보내는 반복문 */
						for (int k = 0; k < writers.size(); k++) {
							if (n.equals(names.get(k))) {
								System.out.println(name);
								writers.get(k).println("MESSAGE" + " /W " + name + ": " + l);
								check1 = 1;
							}
						}
						/* whisper의 형식이 틀렸을 때 */
						if (check2 == 1) {
							for (PrintWriter writer : writers) {
								writer.println("MESSAGE " + name + ": "
										+ input.substring(0, input.length() - name.length() - 1));
							}
						}
						/* 만약 그 서버에 그 사람이 존재하지 않으면 보낸 사람에게 존재하지 않는다고 보내는 반복문 */
						if (check2 != 1 && check1 == 0) {
							for (int k = 0; k < writers.size(); k++) {
								if (key.equals(names.get(k))) {
									System.out.println(name);
									writers.get(k).println("MESSAGE" + " " + n + " is not existence");

								}
							}
						}

					}
					/* whisper가 아닐때 */
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
