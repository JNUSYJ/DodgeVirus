package vita;

import java.awt.*;
import javax.swing.*;

public class Item {
	private Image itemImage = new ImageIcon("src/images/hpitem.png").getImage();
	private int itemX, itemY;
	
	public Item() {
		itemX = (int)(Math.random() * 600);
		itemY = (int)(Math.random() * 500);
	}
	
	public boolean checkCollision(int player_x, int player_y, int player_size) {
		Rectangle ritem = new Rectangle(itemX, itemY, 30, 30);
		Rectangle rplayer = new Rectangle(player_x, player_y, player_size, player_size);

		if (ritem.intersects(rplayer)) { 
			return true;
     	}
     	return false;
	}
	
	public void Draw(Graphics g) {
		g.drawImage(itemImage, itemX, itemY, 30, 30, null);
	}
}
