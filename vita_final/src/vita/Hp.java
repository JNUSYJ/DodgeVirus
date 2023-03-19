package vita;

import java.awt.*;

//Hp클래스를 통한 HP 시스템 구현

class Hp {
	
	private int hp;
	
	public void setHp() {										// 초기 설정
		hp = 100;
	}
	
	public void damaged() {										// 충돌시 HP 감소
		if (hp >= 0)
			hp -= 2;
	}
	
	public void healed() {
		if (hp < 80)
			hp += 20;
		else hp = 100;
	}
	
	public boolean isDied() {										// HP가 0이되면 true
		if (hp <= 0)
			return true;
		return false;
	}
	
	public void Draw(Graphics g) {								// HP바 그리기
		g.setColor(Color.RED);
		g.fillRect(0, 580, (hp * 7), 20);
	}
}