package Client;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import DataStructure.User;

import javax.swing.JLabel;
import javax.swing.JButton;

public class ReadyRoom {

	private JFrame frame;
	User me = new User();
	User other = new User();
	PrintWriter server;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ReadyRoom window = new ReadyRoom();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ReadyRoom() {
		initialize();
	}
	public ReadyRoom(User me, User other,PrintWriter server) {
		this.me = me;
		this.other = other;
		this.server = server;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(200, 200, 550, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setBackground(new Color(242,238,229));
		frame.getContentPane().setLayout(null);
		
		ImageIcon girl = new ImageIcon("C:\\NW_TP\\NetworkTermProject\\girl1.png");
		JLabel lblNewLabel = new JLabel(girl);
		lblNewLabel.setBounds(29, 22, 100, 100);
		frame.getContentPane().add(lblNewLabel);
		
		RoundedButton btnNewButton = new RoundedButton("Ready");
		btnNewButton.setBounds(281, 41, 209, 74);
		btnNewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				TicTacToe a = new TicTacToe(me,server);
				frame.dispose();
				
			}
			
		});
		frame.getContentPane().add(btnNewButton);
		
		ImageIcon man = new ImageIcon("C:\\NW_TP\\NetworkTermProject\\man1.png");
		JLabel lblNewLabel_1 = new JLabel(man);
		lblNewLabel_1.setBounds(29, 166, 100, 100);
		frame.getContentPane().add(lblNewLabel_1);
		
		RoundedButton btnNewButton_1 = new RoundedButton("Ready");
		btnNewButton_1.setBounds(281, 174, 209, 74);
		frame.getContentPane().add(btnNewButton_1);
		
		JLabel lblNewLabel_2 = new JLabel(me.getName());
		lblNewLabel_2.setBounds(131, 41, 83, 32);
		frame.getContentPane().add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel(other.getName());
		lblNewLabel_3.setBounds(131, 188, 83, 32);
		frame.getContentPane().add(lblNewLabel_3);
		
		JLabel lblNewLabel_4 = new JLabel(me.getWin()+"½Â "+me.getdraw()+"¹« "+me.getdraw()+"ÆÐ");
		lblNewLabel_4.setBounds(131, 81, 100, 18);
		frame.getContentPane().add(lblNewLabel_4);
		
		JLabel lblNewLabel_6 = new JLabel(other.getWin()+"½Â "+other.getdraw()+"¹« "+other.getdraw()+"ÆÐ");
		lblNewLabel_6.setBounds(131, 218, 100, 18);
		frame.getContentPane().add(lblNewLabel_6);
		frame.setVisible(true);
		frame.setResizable(true);
	}
}
