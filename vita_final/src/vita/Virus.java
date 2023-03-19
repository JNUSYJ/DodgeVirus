package vita;

import java.awt.*;
import javax.swing.*;

//Virus 클래스를 통한 바이러스 구현

class Virus {
	
	private Image virusImage = new ImageIcon("src/images/virus.png").getImage();	// 바이러스 이미지
	
	private Point pStart;										// 초기값 좌표
	private Point pPos;											// 현재 좌표
	private Point pVel;											// 움직임, 속도
	
	public Virus(int x, int y) {								// 생성자
		pStart = new Point(x,y);
		pPos = new Point(x,y);
		pVel = new Point(x,y);
	}
	
	public void resetPosition() {								// 바이러스 위치 초기화
     pPos.x = pStart.x;
     pPos.y = pStart.y;
 }
	
	public float getAngle(Point pt1, Point pt2) {				// 각도 구하는 메소드
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
	
	public void setMoveValue(int player_x, int player_y) {		// 속도 구하는 메소드
     Point player = new Point(player_x, player_y);
     float angle = getAngle(pPos, player);
     int speed = (int)(Math.random() * 3) + 3;

     pVel.x = (int)(Math.cos(angle) * speed);
     pVel.y = (int)(-Math.sin(angle) * speed);
 }
	
	public boolean moveVirus(int width, int hight) {			// 실제로 움직이게 하는 메소드
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
	
	public boolean checkCollision(int player_x, int player_y, int player_size) {		// 충돌 판정 메소드
		Rectangle rvirus = new Rectangle(pPos.x, pPos.y, 10, 10);
		Rectangle rplayer = new Rectangle(player_x, player_y, player_size, player_size);

		if (rvirus.intersects(rplayer)) { 
			return true;
     	}
     	return false;
	}

	public void Draw(Graphics g) {								// 바이러스 그리기
		g.drawImage(virusImage, pPos.x, pPos.y, 10, 10, null);
	}
}