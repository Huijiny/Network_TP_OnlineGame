package Client;

import java.awt.Color;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.JLabel;

import DataStructure.User;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.io.PrintWriter;

public class Information {
   static String name;
   static int win;
   static int draw;
   static int lose;
    User me;
    User other;
   PrintWriter w;
   private JFrame frame;

   /**
    * Launch the application.
    *//*
       * public static void main(String[] args) { EventQueue.invokeLater(new
       * Runnable() { public void run() { try {
       * 
       * Info window = new Info(user); window.frame.setVisible(true); } catch
       * (Exception e) { e.printStackTrace(); } } }); }
       */

   /**
    * Create the application.
    */
   public Information(String name, int win, int draw, int lose,User a,User b, PrintWriter w) {
      this.name = name;
      this.win = win;
      this.draw = draw;
      this.lose = lose;
      this.me = a;
      this.other=b;
      this.w=w;
      initialize();
   }

   /**
    * Initialize the contents of the frame.
    */
   private void initialize() {
      frame = new JFrame();
      frame.setBounds(100, 100, 350, 200);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.getContentPane().setLayout(null);

      ImageIcon man = new ImageIcon("man1.png");
      JLabel lblNewLabel = new JLabel(man);
      lblNewLabel.setBounds(41, 32, 62, 64);
      frame.getContentPane().add(lblNewLabel);

      JLabel lblNewLabel_1 = new JLabel();

      lblNewLabel_1.setText(name);
      lblNewLabel_1.setBounds(131, 32, 74, 18);
      frame.getContentPane().add(lblNewLabel_1);

      JLabel lblNewLabel_2 = new JLabel();
      lblNewLabel_2.setText(win + "승 " + draw + "무 " + lose + "패");
      lblNewLabel_2.setBounds(131, 78, 83, 18);
      frame.getContentPane().add(lblNewLabel_2);

      AButton btnNewButton = new AButton("대결신청");
      btnNewButton.setBounds(229, 43, 89, 47);
      btnNewButton.addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
			ReadyRoom room = new ReadyRoom(me,other,w);
			System.out.println("infinninfifnifn"+me.getName()+other.getName());
			w.println("match" +me.getName()+","+other.getName());
			frame.dispose();
		}
    	  
      });
      frame.getContentPane().add(btnNewButton);
      frame.setVisible(true);
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      
   }

}