package Client;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import DataStructure.User;
import Login.LoginView;
import Login.MyObjectOutputStream;
import Login.RegisterView;

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
	private static final String JButton = null;
	private static ArrayList<User> registeredUsers = new ArrayList<User>();
	JButton[] sName = new JButton[10];
	String serverAddress;
	Scanner in;
	PrintWriter out;
	RegisterView rv;
	LoginView lv;
	private InputStream is;
	private ObjectInputStream ois;
	private OutputStream os;
	private ObjectOutputStream oos;
	JFrame frame = new JFrame("Chatter");
	JTextField textField = new JTextField(50);
	JTextArea messageArea = new JTextArea(16, 30);
	JButton b = new JButton("Whisper");
	String name;
	JPanel whisperPanel = new JPanel();
	JFrame textFieldforUser = new JFrame("wait");
	JFrame whisperFrame = new JFrame("Whisper");
	JScrollPane scroll = new JScrollPane();
	String info = "";
	String infoName = "";
	String id;
	String pw;
	String myID = "";
	int num;
	User myUser;
	User vUser;
	private static ArrayList<User> users = new ArrayList<User>();

	JPanel statePanel = new JPanel(new GridLayout(10, 10, 10, 2));

	/**
	 * Constructs the client by laying out the GUI and registering a listener with
	 * the textfield so that pressing Return in the listener sends the textfield
	 * contents to the server. Note howev45er that the textfield is initially NOT
	 * editable, and only becomes editable AFTER the client receives the
	 * NAMEACCEPTED message from the server.
	 */

	public ChatClient(String serverAddress) {
		this.serverAddress = serverAddress;

		textField.setEditable(false);
		messageArea.setEditable(false);

		whisperPanel.add(textField);
		whisperPanel.add(b);
		frame.getContentPane().add(whisperPanel, BorderLayout.SOUTH);
		frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);

		frame.pack();

		/* whisper 버튼을 실행시키고 이름을 쳤을때 <name/>형태로 바꿔주는 acctionlistener */
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
				 * server에 보낼때 자신의 이름을 보내어 누가 보냈는지 알수 있게 ^와 name을 같이 보낸다. (whisper로 보낼때 없는 사람을
				 * 보낼 수 도 있기때문에 is not existence를 보내기위해)
				 */
				out.println(textField.getText() + "^" + name);
				textField.setText("");
			}
		});
	}

	private class CreateButtonListener implements ActionListener {

		public CreateButtonListener() {
		}

		public void actionPerformed(ActionEvent e) {

			for (int i = 0; i < num; i++) {
				if (e.getSource() == sName[i]) {
					infoName = sName[i].getText();
					System.out.println("print" + infoName + "^" + name);
					out.println("GETINFO " + infoName + "^" + name);

				}
			}

		}
	}

	public void clickButton(String infoName, String content) {

		String getName;////// 내정보저장함
		int win;
		int draw, lose;
		myUser = new User();
		User sUser = new User();
		System.out.println("asdasdas" + content);
		String[] array = content.split("\\^");
		System.out.println(array[0]);
		System.out.println("1 " + array[1]);
		System.out.println("2 " + array[2]);
		System.out.println("3 " + array[3]);

		getName = array[0];
		win = Integer.parseInt(array[1]);
		draw = Integer.parseInt(array[2]);
		lose = Integer.parseInt(array[3]);

		//// 내정보 레지스터 돌면서 저장함.ㅋㅋㅋ
		for (User user2 : registeredUsers) {
			if (user2.getName().equals(getName)) {
				sUser = user2;

			}

		}
		for (int i = 0; i < registeredUsers.size(); i++) {
			if (registeredUsers.get(i).getName().equals(name)) {
				myUser = registeredUsers.get(i);

			}
		}

		Information infomation = new Information(getName, win, draw, lose, myUser, sUser, out);

	}

	private void buttonCreate(String name) {
		sName[num] = new JButton(name);
		statePanel.add(sName[num]);
		frame.getContentPane().add(scroll.add(statePanel), BorderLayout.AFTER_LINE_ENDS);
		frame.pack();
		sName[num].setActionCommand(name);
		sName[num].addActionListener(new CreateButtonListener());
		num++;
	}

	private void buttonRemove() {
		statePanel.removeAll();
		frame.pack();
	}

	/* whisper로 보낼 이름을 설정하는 UI */
	private String getWhisperName() {
		return JOptionPane.showInputDialog(frame, "Whisper name:", "Whisper", JOptionPane.PLAIN_MESSAGE);
	}

	private void run(String ip, int port) throws IOException {
		Socket socket = null;
		try {
			try {
				socket = new Socket(ip, port);
			} catch (Exception e) {
				System.out.println("서버 소켓 오류");
			}
			try {

				in = new Scanner(socket.getInputStream());
				out = new PrintWriter(socket.getOutputStream(), true);

			} catch (Exception e) {
				System.out.println("스트림 오류");
			} /* 서버로 오는 값을 받는 protocol */
			/* 서버로 오는 값을 받는 protocol */
			while (in.hasNextLine()) {
				String line = in.nextLine();
				System.out.println("들어왔다." + line);

				/* 처음에 들어왔을때 접속메세지 보내는 조건문 */
				if (line.startsWith("LOGIN")) {

					lv = new LoginView();
					lv.setVisible(true);
					// 로그인 눌렀을 경우

					lv.loginBtn.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							String result;
							String id = lv.idField.getText();
							name = id;
							String pass = String.valueOf(lv.pw.getPassword());
							System.out.println("클라이언트 : " + id + " " + pass);
							out.println("TRYLOGIN " + id + "/" + pass);
							lv.setVisible(false);
						}
					}); // 회원가입 눌렀을 경우.

					lv.joinBtn.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							rv = new RegisterView();
							rv.setVisible(true);
							if (rv != null) {
								rv.j1.addActionListener(new ActionListener() {

									@Override
									public void actionPerformed(ActionEvent e) {

										// TODO Auto-generated method stub
										try {
											// 성공하면 아래코드 실행. 실패하면 틀렸다고 다시 쓰라고 option알려주기.
											boolean success = false;
											User user = new User(rv.t1.getText(), rv.t2.getText(), rv.t3.getText());
											System.out.println(user.getID());
											for (int i = 0; i < registeredUsers.size(); i++) {
												success = true;
												if (registeredUsers.get(i).getID().equals(user.getID())) {
													System.out.println("똑같냐?????? "
															+ registeredUsers.get(i).getID().equals(user.getID()));
													JOptionPane.showMessageDialog(null, "아이디 다른 걸로 교체하셈.");
													success = false;
													break;
												}

											}
											success = true;
											if (success) {
												//// server에 getuserinfo 다시 요청하고.
												out.println("GETUSERMODIFIEDINFO" + user.getName() + "/" + user.getID()
														+ "/" + user.getPasswd());
												rv.dispose();
												lv.dispose();
											}
											success = false;
										} catch (Exception ex) {
											JOptionPane.showMessageDialog(null, "회원가입에 실패하였습니다.");
										}
									}

								});
								rv.j2.addActionListener(new ActionListener() {

									@Override
									public void actionPerformed(ActionEvent e) {
										// TODO Auto-generated method stub
										rv.dispose();
									}

								});
							}
						}

					});

				} else if (line.startsWith("LSUCCESS")) {

					////// 로그인에 성공했다는 표시 오면 성운이 대기실 frame띄워주기.
					this.frame.setTitle("Chatter - " + line.substring(8));
					textField.setEditable(true);

					/* chat message를 출력하는 조건문 */

				} else if (line.startsWith("REMOVEREGISTER")) {
					System.out.println("삭제했어?");
					registeredUsers.removeAll(registeredUsers);
				} else if (line.startsWith("GETREGISTEREDUSERINFO ")) {
					/* 맨 처음에 서버가 올라가면 현재까지 가입 유저들 정보 싹 다시 올려줘서 tmpuser에 add하는 코드 */
					User tmpuser = new User();

					line = line.substring(22);
					String[] ary = line.split(",");

					tmpuser.setName(ary[0]);
					tmpuser.setID(ary[1]);
					tmpuser.setPasswd(ary[2]);
					tmpuser.setWin(Integer.parseInt(ary[3]));
					tmpuser.setLose(Integer.parseInt(ary[4]));
					tmpuser.setDraw(Integer.parseInt(ary[5]));
					System.out.println("뭐가들어와?" + tmpuser);
					registeredUsers.add(tmpuser);
				} else if (line.startsWith("MESSAGEjoin")) {
					buttonCreate(line.substring(12));
				} else if (line.startsWith("MESSAGE")) {
					messageArea.append(line.substring(8) + "\n");
				} else if (line.startsWith("AddButton")) {
					buttonCreate(line.substring(10));
				} else if (line.startsWith("Remove")) {
					buttonRemove();
				} else if (line.startsWith("Info")) {
					System.out.println("정보" + line.substring(5));
					info = line.substring(5);
					System.out.println(info);
					clickButton(infoName, info);
				} else if (line.startsWith("match")) {
					
					String vName= line.substring(5);

					vUser = new User();
					
					
					for (int i = 0; i < registeredUsers.size(); i++) {

						if (registeredUsers.get(i).getName().equals(vName)) {
							vUser = registeredUsers.get(i);

						}
					}
					for (int i = 0; i < registeredUsers.size(); i++) {

						if (registeredUsers.get(i).getName().equals(name)) {
							myUser = registeredUsers.get(i);
						}
					}
					

					System.out.println("my user : " + myUser);
					System.out.println("my vuser : " + vUser);
					ReadyRoom room1 = new ReadyRoom(myUser, vUser,out);

				}

			}
		} finally

		{
			frame.setVisible(false);
			frame.dispose();
		}
	}

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

		ChatClient client = new ChatClient(ip);
		client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.frame.setVisible(true);
		client.run(ip, port);
	}

}