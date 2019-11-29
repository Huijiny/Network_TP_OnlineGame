package Server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import DataStructure.User;
import Login.FileWhatcher;
import Login.MyObjectOutputStream;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

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
	private static ArrayList<User> users = new ArrayList<User>();// ��� Ŭ���̾�Ʈ�鿡�� ������Ʈ�Ǿ����. �� ó���� ���� �������ڸ��� ������������ �� ��������.

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
		FileWhatcher fw = new FileWhatcher();// filewhatcher��� Ŭ������ �� ������ �ִ� ������ �ϳ��� ��ȭ�� ������ ��� ��ȭ�� �����ϴ� Ŭ������.
		try {
			fw.init();// filewhatcherŬ���� ������ init()���� �� �ϴ��� �� �� ����.

			getUserInfo();// server�� ������ �����͸� �����ص�.
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("eee1");
			System.out.println(e.getMessage());
		}
		ExecutorService pool = Executors.newFixedThreadPool(500); // multithread ����
		try (ServerSocket listener = new ServerSocket(port)) {
			while (true) {
				pool.execute(new Handler(listener.accept())); // mutithread ����
			}
		}
	}

	static void getUserInfo() {
		
		FileInputStream fis = null;
		ObjectInputStream ois = null;

		int c;

		try {
			fis = new FileInputStream("userinfo.txt");
			ois = new ObjectInputStream(fis);

			while ((c = ois.read()) == -1) {

				User tmp = (User) ois.readObject();
				System.out.println(tmp);
				String pw = tmp.getPasswd();
				System.out.println(pw);
				pw = Decrypt(pw, "key");
				System.out.println(pw);
				tmp.setPasswd(pw);
				users.add(tmp);

			}
			fis.close();
			ois.close();
		} catch (Exception e) {
			// System.out.println(e.getMessage());
		}
		// ���� ���� �޾ƿ����� Ȯ�� �ڵ�. ���߿� �����.
		for (int i = 0; i < users.size(); i++) {
			System.out.println(users.get(i));
		}
	}

	/**
	 * The client handler task.
	 */
	private static class Handler implements Runnable {
		private String name;
		private String id;
		private String pw;
		private Socket socket;
		private Scanner in;
		private static PrintWriter out;
		private InputStream is;
		private ObjectInputStream ois;
		private OutputStream os;
		private ObjectOutputStream oos;

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
		void sendinfo() {

			for (PrintWriter writer : writers) {
				writer.println("REMOVEREGISTER");
				for (User user : users) {
					writer.println("GETREGISTEREDUSERINFO " + user.getName() + "," + user.getID() + ","
							+ user.getPasswd() + "," + user.getWin() + "," + user.getlose() + "," + user.getdraw());

				}
			}
			for (User user : users) {
				out.println("GETREGISTEREDUSERINFO " + user.getName() + "," + user.getID() + "," + user.getPasswd()
						+ "," + user.getWin() + "," + user.getlose() + "," + user.getdraw());

			}
		}

		public void run() {
			String key1 = "key";
			try {
				in = new Scanner(socket.getInputStream());
				out = new PrintWriter(socket.getOutputStream(), true);

				sendinfo();

				// Keep requesting a name until we get a unique one.
				while (true) {
					int i = 0;
					out.println("LOGIN");
					String s = in.nextLine();
					if (s.startsWith("GETUSERMODIFIEDINFO")) {
						String a = s.substring(19);
						System.out.println("�����´ٰ�?" + a);
						String[] array = a.split("/");
						User user = new User();
						user.setName(array[0]);
						user.setID(array[1]);
						// user.setPasswd(array[2]);
						// PasswdEncryt encypt = new PasswdEncryt();
						String pw = Encrypt(array[2], key1);
						System.out.println(pw);
						user.setPasswd(pw);
						setUserInfo(user);
						users.removeAll(users);
						getUserInfo();

						sendinfo();
						// �̰� ������ ��ü �� ��ε�ĳ�������ֱ�.
					}
					if (s.startsWith("TRYLOGIN ")) {
						System.out.println(s + "��� �Ծ��");
						String a = s.substring(10);
						id = s.substring(9, s.indexOf("/"));
						pw = s.substring(s.indexOf("/") + 1);
						System.out.println(id + " " + pw);
						if (id == null || pw == null) {

							return;
						}
						boolean suc = false;
						boolean fail = false;
						for (User user : users) {

							if (id.length() > 0 && pw.length() > 0 && user.getID().equals(id)
									&& user.getPasswd().equals(pw)) {

								name = user.getName();
								names.add(name);
								System.out.println(name);
								out.println("LSUCCESS" + name);
								suc = true;

								break;
							} else {
								fail = true;
							}
							continue;
						}
						if (suc) {
							break;
						}
						if (fail) {
							continue;
						}

					}

				}

				for (PrintWriter writer : writers) {
					writer.println("MESSAGEjoin " + name);
					writer.println("MESSAGE " + name + " has joined");
					writer.println("REMOVEREGISTER");
					for (User user : users) {
						writer.println("GETREGISTEREDUSERINFO " + user.getName() + "," + user.getID() + ","
								+ user.getPasswd() + "," + user.getWin() + "," + user.getlose() + "," + user.getdraw());

					}
				}

				writers.add(out);
				out.println("REMOVEREGISTER");
				for (String i : names) {
					out.println("AddButton " + i);
				}
				for (User user : users) {
					out.println("GETREGISTEREDUSERINFO " + user.getName() + "," + user.getID() + "," + user.getPasswd()
							+ "," + user.getWin() + "," + user.getlose() + "," + user.getdraw());

				}
				// Accept messages from this client and broadcast them.
				while (true) {
					String input = in.nextLine();
					if (input.toLowerCase().startsWith("/quit")) {
						return;
					} else if (input.toLowerCase().startsWith("getinfo")) {

						int k = 0;
						String infoName = "";
						String sendName = "";
						for (int i = 0; i < input.length(); i++) {
							if (input.charAt(i) == '^') {
								k = i;
							}
						}
						sendName = input.substring(k + 1);
						infoName = input.substring(8, k);
						System.out.println("sendName" + sendName);
						System.out.println("infoName" + infoName);
						for (User user : users) {
							if (user.getName().equalsIgnoreCase(infoName)) {
								for (int r = 0; r < writers.size(); r++) {
									if (sendName.equals(names.get(r))) {
										System.out.println(name);
										writers.get(r).println("Info " + user.getName() + "^" + user.getWin() + "^"
												+ user.getdraw() + "^" + user.getlose());
										System.out.println(user.getName() + "^" + user.getWin() + "^" + user.getdraw()
												+ "^" + user.getlose());
									}
								}

							}
						}

					} else if(input.startsWith("CHANGED_SCORE_WIN")){
						String user_name= input.substring(17);
						System.out.println(user_name+"win");
						
						for(int i=0;i<users.size();i++) {
							if(users.get(i).getName().equals(user_name)) {
								users.get(i).win();
							}
						}
						for (PrintWriter writer : writers) {
							writer.println("REMOVEREGISTER");
							for (User user : users) {
								writer.println("GETREGISTEREDUSERINFO " + user.getName() + "," + user.getID() + ","
										+ user.getPasswd() + "," + user.getWin() + "," + user.getlose() + "," + user.getdraw());

							}
						}
						//updateText();
						
						
					}  else if(input.startsWith("CHANGED_SCORE_LOSE")){
						String user_name= input.substring(18);
						System.out.println(user_name+"lose");
						for(int i=0;i<users.size();i++) {
							if(users.get(i).getName().equals(user_name)) {
								users.get(i).lose();
							}
						}
						for (PrintWriter writer : writers) {
							writer.println("REMOVEREGISTER");
							for (User user : users) {
								writer.println("GETREGISTEREDUSERINFO " + user.getName() + "," + user.getID() + ","
										+ user.getPasswd() + "," + user.getWin() + "," + user.getlose() + "," + user.getdraw());

							}
						}
						//updateText();

						
					}  else if(input.startsWith("CHANGED_SCORE_DRAW")){
						String user_name= input.substring(18);
						System.out.println(user_name+"draw");
						for(int i=0;i<users.size();i++) {
							if(users.get(i).getName().equals(user_name)) {
								users.get(i).draw();
							}
						}
						for (PrintWriter writer : writers) {
							writer.println("REMOVEREGISTER");
							for (User user : users) {
								writer.println("GETREGISTEREDUSERINFO " + user.getName() + "," + user.getID() + ","
										+ user.getPasswd() + "," + user.getWin() + "," + user.getlose() + "," + user.getdraw());

							}
						}
						//updateText();
						
						
					}  else if (input.startsWith("match")) {
						String s = input.substring(5);
						String[] array = s.split(",");
						
						for (int k = 0; k < writers.size(); k++) {
							if (array[1].equals(names.get(k))) {
								System.out.println(name);
								writers.get(k).println("match"+array[0]);
								
							}
						}
						System.out.println(s);

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
						writer.println("Remove" + name);

						for (String i : names) {
							writer.println("AddButton " + i);
						}

					}

				}
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
	}
	static void updateText()  {
		//���� �����ϱ� �� �� �����ֱ�.
		File file = new File("userinfo.txt");
		if(file.exists()) {
			if(file.delete())System.out.println("���ϻ���");
		}
		/*
		PrintWriter writer;
		try {
			writer = new PrintWriter(file);
			writer.print("");
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		for(int i=0;i<users.size();i++) {
			setUserInfo(users.get(i));
			System.out.println(users.get(i));
		}
	}

	static void setUserInfo(User user) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		try {
			if (new File("userinfo.txt").exists()) {
				fos = new FileOutputStream("./userinfo.txt", true);
				oos = new MyObjectOutputStream(fos);
			} else {
				fos = new FileOutputStream("./userinfo.txt");
				oos = new ObjectOutputStream(fos);
			}
			oos.writeObject(user);
			oos.flush();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				oos.close();
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	public static String Decrypt(String text, String key) throws Exception

	{

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

		byte[] keyBytes = new byte[16];

		byte[] b = key.getBytes("UTF-8");

		int len = b.length;

		if (len > keyBytes.length)
			len = keyBytes.length;

		System.arraycopy(b, 0, keyBytes, 0, len);

		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

		IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);

		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

		BASE64Decoder decoder = new BASE64Decoder();

		byte[] results = cipher.doFinal(decoder.decodeBuffer(text));

		return new String(results, "UTF-8");

	}

	public static String Encrypt(String text, String key) throws Exception

	{

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

		byte[] keyBytes = new byte[16];

		byte[] b = key.getBytes("UTF-8");

		int len = b.length;

		if (len > keyBytes.length)
			len = keyBytes.length;

		System.arraycopy(b, 0, keyBytes, 0, len);

		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

		IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);

		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

		byte[] results = cipher.doFinal(text.getBytes("UTF-8"));

		BASE64Encoder encoder = new BASE64Encoder();

		return encoder.encode(results);

	}
}