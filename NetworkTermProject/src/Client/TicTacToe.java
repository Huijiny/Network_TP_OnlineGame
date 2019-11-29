package Client;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.jws.soap.SOAPBinding.Use;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JTextField;

import DataStructure.User;

import javax.swing.JTextArea;
import javax.swing.JButton;

public class TicTacToe implements Runnable {
	User user ;
	PrintWriter server;
   private String ip = "localhost";
   private int port = 22222;
   private Scanner scanner = new Scanner(System.in);
   private JFrame frame;
   private final int WIDTH = 506;
   private final int HEIGHT = 527;
   private Thread thread;

   private Painter painter;
   private Socket socket;
   private DataOutputStream dos;
   private DataInputStream dis;

   private ServerSocket serverSocket;

   private BufferedImage board;
   private BufferedImage redX;
   private BufferedImage blueX;
   private BufferedImage redCircle;
   private BufferedImage blueCircle;

   Scanner in = null;
   PrintWriter out;
   JTextArea messageArea = null;
      
   private String[] spaces = new String[9];

   private boolean yourTurn = false;
   private boolean circle = true;
   private boolean accepted = false;
   private boolean unableToCommunicateWithOpponent = false;
   private boolean won = false;
   private boolean enemyWon = false;
   private boolean tie = false;

   private int lengthOfSpace = 160;
   private int errors = 0;
   private int firstSpot = -1;
   private int secondSpot = -1;

   private Font font = new Font("Verdana", Font.BOLD, 32);
   private Font smallerFont = new Font("Verdana", Font.BOLD, 20);
   private Font largerFont = new Font("Verdana", Font.BOLD, 50);

   private String waitingString = "Waiting for another player";
   private String unableToCommunicateWithOpponentString = "Unable to communicate with opponent.";
   private String wonString = "You won!";
   private String enemyWonString = "Opponent won!";
   private String tieString = "Game ended in a tie.";

   private int[][] wins = new int[][] { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 }, { 0, 4, 8 }, { 2, 4, 6 } };
   private JTextField textField;

   /**
    * <pre>
    * 0, 1, 2 
    * 3, 4, 5 
    * 6, 7, 8
    * </pre>
    */
   
   public TicTacToe(User me,PrintWriter server) {
      this.user = me;
      this.server = server;
       Socket socket;
         try {
            socket = new Socket("localhost", 59001);
            in = new Scanner(socket.getInputStream());
             out = new PrintWriter(socket.getOutputStream(), true);
                  
         } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
         }

      ip = "localhost";
      System.out.println("IP Accepted");
      port = 1234;
      System.out.println("Port Accepted");
      
      while (port < 1 || port > 65535) {
         System.out.println("The port you entered was invalid, please input another port: ");
         port = scanner.nextInt();
      }

      loadImages();

      painter = new Painter();
      painter.setSize(500, 500);
      //painter.setPreferredSize(new Dimension(WIDTH, HEIGHT));

      if (!connect()) initializeServer();

      frame = new JFrame();
      frame.setTitle("Tic-Tac-Toe");
      //frame.setContentPane(painter);
      frame.setSize(1000,500);
      frame.setLocationRelativeTo(null);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setResizable(true);
      //frame.getContentPane().setLayout(null);
      JPanel panel1 = (JPanel) frame.getContentPane();
      panel1.setLayout(null);
      panel1.setSize(500,500);
      panel1.setBackground(Color.BLACK);
      panel1.add(painter);
      
      JPanel panel2 = new JPanel();
      panel2.setLocation(500, 0);
      panel2.setBackground(Color.PINK);
      panel2.setSize(500,500);
      frame.getContentPane().add(panel2);
      panel2.setLayout(null);
      
      textField = new JTextField();
      textField.setBounds(14, 424, 451, 24);
      panel2.add(textField);
      textField.setColumns(10);
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               // TODO Auto-generated method stub
                 out.println(textField.getText());
                       textField.setText("");
            }
             });

      messageArea= new JTextArea();
      messageArea.setBounds(14, 12, 450, 400);
      messageArea.setSize(450, 410);
      panel2.add(messageArea);
      frame.setVisible(true);

      thread = new Thread(this, "TicTacToe");
      thread.start();
   }





public void run() {
      ExecutorService executor = Executors.newSingleThreadExecutor();
      CompletableFuture.runAsync(()->{
         while(in.hasNextLine()) {
               String line = in.nextLine();
               if (line.startsWith("SUBMITNAME")) {
                   out.println(user.getName());
               } else if (line.startsWith("NAMEACCEPTED")) {
                       System.out.println("�̸� accept�ƴ�"+line.substring(13));
                      } else if (line.startsWith("MESSAGE")) {
                      messageArea.append(line.substring(8) + "\n");
                  }
            }
      },executor).thenRun(()->System.out.println("��"));
      
      while (true) {
         tick();
         painter.repaint();

         if (!circle && !accepted) {
            listenForServerRequest();
         }
         /*
            if(in.hasNextLine()) {
            String line = in.nextLine();
           if (line.startsWith("NAMEACCEPTED")) {
                   
                  } else if (line.startsWith("MESSAGE")) {
                  messageArea.append(line.substring(8) + "\n");
              }
        }*/



      }
   }

   private void render(Graphics g) {
      g.drawImage(board, 0, 0, null);
      if (unableToCommunicateWithOpponent) {
         g.setColor(Color.RED);
         g.setFont(smallerFont);
         Graphics2D g2 = (Graphics2D) g;
         g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
         int stringWidth = g2.getFontMetrics().stringWidth(unableToCommunicateWithOpponentString);
         g.drawString(unableToCommunicateWithOpponentString, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
         return;
      }

      if (accepted) {
         for (int i = 0; i < spaces.length; i++) {
            if (spaces[i] != null) {
               if (spaces[i].equals("X")) {
                  if (circle) {
                     g.drawImage(redX, (i % 3) * lengthOfSpace + 10 * (i % 3), (int) (i / 3) * lengthOfSpace + 10 * (int) (i / 3), null);
                  } else {
                     g.drawImage(blueX, (i % 3) * lengthOfSpace + 10 * (i % 3), (int) (i / 3) * lengthOfSpace + 10 * (int) (i / 3), null);
                  }
               } else if (spaces[i].equals("O")) {
                  if (circle) {
                     g.drawImage(blueCircle, (i % 3) * lengthOfSpace + 10 * (i % 3), (int) (i / 3) * lengthOfSpace + 10 * (int) (i / 3), null);
                  } else {
                     g.drawImage(redCircle, (i % 3) * lengthOfSpace + 10 * (i % 3), (int) (i / 3) * lengthOfSpace + 10 * (int) (i / 3), null);
                  }
               }
            }
         }
         if (won || enemyWon) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(10));
            g.setColor(Color.BLACK);
            g.drawLine(firstSpot % 3 * lengthOfSpace + 10 * firstSpot % 3 + lengthOfSpace / 2, (int) (firstSpot / 3) * lengthOfSpace + 10 * (int) (firstSpot / 3) + lengthOfSpace / 2, secondSpot % 3 * lengthOfSpace + 10 * secondSpot % 3 + lengthOfSpace / 2, (int) (secondSpot / 3) * lengthOfSpace + 10 * (int) (secondSpot / 3) + lengthOfSpace / 2);

            g.setColor(Color.RED);
            g.setFont(largerFont);
            if (won) {
               int stringWidth = g2.getFontMetrics().stringWidth(wonString);
               g.drawString(wonString, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
               
            } else if (enemyWon) {
               int stringWidth = g2.getFontMetrics().stringWidth(enemyWonString);
               g.drawString(enemyWonString, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
               
            }
         }
         if (tie) {
            Graphics2D g2 = (Graphics2D) g;
            g.setColor(Color.BLACK);
            g.setFont(largerFont);
            int stringWidth = g2.getFontMetrics().stringWidth(tieString);
            g.drawString(tieString, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
         }
      } else {
         g.setColor(Color.RED);
         g.setFont(font);
         Graphics2D g2 = (Graphics2D) g;
         g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
         int stringWidth = g2.getFontMetrics().stringWidth(waitingString);
         g.drawString(waitingString, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
      }

   }

   private void tick() {
      if (errors >= 10) unableToCommunicateWithOpponent = true;

      if (!yourTurn && !unableToCommunicateWithOpponent) {
         try {
            int space = dis.readInt();
            if (circle) spaces[space] = "X";
            else spaces[space] = "O";
            checkForEnemyWin();
            checkForTie();
            yourTurn = true;
         } catch (IOException e) {
            e.printStackTrace();
            errors++;
         }
      }
   }

   private void checkForWin() {
      for (int i = 0; i < wins.length; i++) {
         if (circle) {
            if (spaces[wins[i][0]] == "O" && spaces[wins[i][1]] == "O" && spaces[wins[i][2]] == "O") {
               firstSpot = wins[i][0];
               secondSpot = wins[i][2];
               won = true;
               user.win();
               server.println("CHANGED_SCORE_WIN"+user.getName());
               System.out.println(user.getName()+"�� �̰ܼ� "+user.getWin()+"�� ����!!!!!!!!!!!!");
            }
         } else {
            if (spaces[wins[i][0]] == "X" && spaces[wins[i][1]] == "X" && spaces[wins[i][2]] == "X") {
               firstSpot = wins[i][0];
               secondSpot = wins[i][2];
               won = true;
               user.win();

               System.out.println("�����������"+server);
               System.out.println("�����̸�����"+user.getName());
               server.println("CHANGED_SCORE_WIN"+user.getName());
               System.out.println(user.getName()+"�� �̰ܼ� "+user.getWin()+"�� ����!!!!!!!!!!!!");
            }
         }
      }
   }

   private void checkForEnemyWin() {
      for (int i = 0; i < wins.length; i++) {
         if (circle) {
            if (spaces[wins[i][0]] == "X" && spaces[wins[i][1]] == "X" && spaces[wins[i][2]] == "X") {
               firstSpot = wins[i][0];
               secondSpot = wins[i][2];
               enemyWon = true;
               user.lose();
               server.println("CHANGED_SCORE_LOSE"+user.getName());
               System.out.println(user.getName()+"�� ���� "+user.getlose()+"�� ����!!!!!!!!!!!!");
            }
         } else {
            if (spaces[wins[i][0]] == "O" && spaces[wins[i][1]] == "O" && spaces[wins[i][2]] == "O") {
               firstSpot = wins[i][0];
               secondSpot = wins[i][2];
               enemyWon = true;
               user.lose();
               System.out.println("�����������"+server);
               System.out.println("�����̸�����"+user.getName());
               server.println("CHANGED_SCORE_LOSE"+user.getName());
               System.out.println(user.getName()+"�� ���� "+user.getlose()+"�� ����!!!!!!!!!!!!");
            }
         }
      }
   }

   private void checkForTie() {
      for (int i = 0; i < spaces.length; i++) {
         if (spaces[i] == null) {
            return;
         }
      }
      tie = true;
      user.draw();
      server.println("CHANGED_SCORE_DRAW"+user.getName());
      System.out.println(user.getName()+"�� ��ܼ� "+user.getdraw()+"�� ����!!!!!!!!!!!!");
   }

   private void listenForServerRequest() {
      Socket socket = null;
      try {
         socket = serverSocket.accept();
         dos = new DataOutputStream(socket.getOutputStream());
         dis = new DataInputStream(socket.getInputStream());
         accepted = true;
         System.out.println("CLIENT HAS REQUESTED TO JOIN, AND WE HAVE ACCEPTED");
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private boolean connect() {
      try {
         socket = new Socket(ip, port);
         dos = new DataOutputStream(socket.getOutputStream());
         dis = new DataInputStream(socket.getInputStream());
         accepted = true;
      } catch (IOException e) {
         System.out.println("Unable to connect to the address: " + ip + ":" + port + " | Starting a server");
         return false;
      }
      System.out.println("Successfully connected to the server.");
      return true;
   }

   private void initializeServer() {
      try {
         serverSocket = new ServerSocket(port, 8, InetAddress.getByName(ip));
      } catch (Exception e) {
         e.printStackTrace();
      }
      yourTurn = true;
      circle = false;
   }

   private void loadImages() {
      try {

         board = ImageIO.read(new File("C:\\NW_TP\\NetworkTermProject\\board.png"));
         redX = ImageIO.read(new File("C:\\NW_TP\\NetworkTermProject\\redX.png"));
         redCircle = ImageIO.read(new File("C:\\NW_TP\\NetworkTermProject\\redCircle.png"));
         blueX = ImageIO.read(new File("C:\\NW_TP\\NetworkTermProject\\blueX.png"));
         blueCircle = ImageIO.read(new File("C:\\NW_TP\\NetworkTermProject\\blueCircle.png"));
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
/*
   @SuppressWarnings("unused")
   public static void main(String[] args) {
      TicTacToe ticTacToe = new TicTacToe();
   }
*/
   private class Painter extends JPanel implements MouseListener {
      private static final long serialVersionUID = 1L;

      public Painter() {
         setFocusable(true);
         requestFocus();
         setBackground(Color.WHITE);
         addMouseListener(this);
      }

      @Override
      public void paintComponent(Graphics g) {
         super.paintComponent(g);
         render(g);
      }

      @Override
      public void mouseClicked(MouseEvent e) {
         if (accepted) {
            if (yourTurn && !unableToCommunicateWithOpponent && !won && !enemyWon) {
               int x = e.getX() / lengthOfSpace;
               int y = e.getY() / lengthOfSpace;
               y *= 3;
               int position = x + y;

               if (spaces[position] == null) {
                  if (!circle) spaces[position] = "X";
                  else spaces[position] = "O";
                  yourTurn = false;
                  repaint();
                  Toolkit.getDefaultToolkit().sync();

                  try {
                     dos.writeInt(position);
                     dos.flush();
                  } catch (IOException e1) {
                     errors++;
                     e1.printStackTrace();
                  }

                  System.out.println("DATA WAS SENT");
                  checkForWin();
                  checkForTie();

               }
            }
         }
      }

      @Override
      public void mousePressed(MouseEvent e) {

      }

      @Override
      public void mouseReleased(MouseEvent e) {

      }

      @Override
      public void mouseEntered(MouseEvent e) {

      }

      @Override
      public void mouseExited(MouseEvent e) {

      }

   }
}