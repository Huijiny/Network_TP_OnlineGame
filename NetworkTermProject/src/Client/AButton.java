package Client;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

public class AButton extends JButton { 
		public AButton() {
			super(); 
			decorate();
			} 
		public AButton(String text) {
			super(text);
			 decorate();
			}
		public AButton(Action action) {
			super(action);
			 decorate();
			}
		public AButton(Icon icon) {
			super(icon);
			 decorate();
			}
		public AButton(String text, Icon icon) {
			super(text, icon); 
			 decorate();
			}
		protected void decorate() {
			setBorderPainted(false);
			setOpaque(false); 
			}
		
		@Override protected void paintComponent(Graphics g) {
			Font bold_ac = new Font("¸¼Àº °íµñ",Font.BOLD,15);
					
			int width = getWidth();
			int height = getHeight();
			
			g.setFont(bold_ac);
			
			g.setColor(new Color(0,153,153));
			Graphics2D graphics = (Graphics2D) g;
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
			if (getModel().isArmed()) {
				graphics.setColor(graphics.getColor().darker());
				} else if (getModel().isRollover()) {
					graphics.setColor(new Color(0,152,152));
					} else {
						graphics.setColor(graphics.getColor());
						}
						
			graphics.fillRoundRect(0, 0, width, height, 10, 10);
			FontMetrics fontMetrics = graphics.getFontMetrics();
			Rectangle stringBounds = fontMetrics.getStringBounds(this.getText(), graphics).getBounds();
			
			int textX = (width - stringBounds.width) / 2;
			int textY = (height - stringBounds.height) / 2 + fontMetrics.getAscent();
			
			graphics.setColor(getForeground());
			//graphics.setFont(getFont());
			graphics.drawString(getText(), textX, textY);
			graphics.dispose();
			g.dispose();
			
			super.paintComponent(g);
			}


		}