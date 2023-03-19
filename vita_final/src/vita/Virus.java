package vita;

import java.awt.*;
import javax.swing.*;

//Virus Ŭ������ ���� ���̷��� ����

class Virus {
	
	private Image virusImage = new ImageIcon("src/images/virus.png").getImage();	// ���̷��� �̹���
	
	private Point pStart;										// �ʱⰪ ��ǥ
	private Point pPos;											// ���� ��ǥ
	private Point pVel;											// ������, �ӵ�
	
	public Virus(int x, int y) {								// ������
		pStart = new Point(x,y);
		pPos = new Point(x,y);
		pVel = new Point(x,y);
	}
	
	public void resetPosition() {								// ���̷��� ��ġ �ʱ�ȭ
     pPos.x = pStart.x;
     pPos.y = pStart.y;
 }
	
	public float getAngle(Point pt1, Point pt2) {				// ���� ���ϴ� �޼ҵ�
     float fX, fY;
     float fAngle;

     fX = pt2.x - pt1.x;
     fY = pt2.y - pt1.y;

     fAngle = (float) Math.atan(-fY / fX);

     if (fX < 0)
     {
         fAngle += Math.PI;
     }

     if (pt2.x >= pt1.x && pt2.y >= pt1.y)
     {
         fAngle += 2 * Math.PI;
     }

     return fAngle;
 }
	
	public void setMoveValue(int player_x, int player_y) {		// �ӵ� ���ϴ� �޼ҵ�
     Point player = new Point(player_x, player_y);
     float angle = getAngle(pPos, player);
     int speed = (int)(Math.random() * 3) + 3;

     pVel.x = (int)(Math.cos(angle) * speed);
     pVel.y = (int)(-Math.sin(angle) * speed);
 }
	
	public boolean moveVirus(int width, int hight) {			// ������ �����̰� �ϴ� �޼ҵ�
     pPos.x += pVel.x;
     pPos.y += pVel.y;

     if ((pPos.x + 5) < 0 || (pPos.x - 5) > width) {
         pPos.x = pStart.x;
         pPos.y = pStart.y;
         
         return false;
     }
     else if ((pPos.y + 5 < 0) || (pPos.y - 5) > hight) {
         pPos.x = pStart.x;
         pPos.y = pStart.y;
         
         return false;
     }

     return true;
 }
	
	public boolean checkCollision(int player_x, int player_y, int player_size) {		// �浹 ���� �޼ҵ�
		Rectangle rvirus = new Rectangle(pPos.x, pPos.y, 10, 10);
		Rectangle rplayer = new Rectangle(player_x, player_y, player_size, player_size);

		if (rvirus.intersects(rplayer)) { 
			return true;
     	}
     	return false;
	}

	public void Draw(Graphics g) {								// ���̷��� �׸���
		g.drawImage(virusImage, pPos.x, pPos.y, 10, 10, null);
	}
}