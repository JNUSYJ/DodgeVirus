package vita;

import java.awt.*;

//HpŬ������ ���� HP �ý��� ����

class Hp {
	
	private int hp;
	
	public void setHp() {										// �ʱ� ����
		hp = 100;
	}
	
	public void damaged() {										// �浹�� HP ����
		if (hp >= 0)
			hp -= 2;
	}
	
	public void healed() {
		if (hp < 80)
			hp += 20;
		else hp = 100;
	}
	
	public boolean isDied() {										// HP�� 0�̵Ǹ� true
		if (hp <= 0)
			return true;
		return false;
	}
	
	public void Draw(Graphics g) {								// HP�� �׸���
		g.setColor(Color.RED);
		g.fillRect(0, 580, (hp * 7), 20);
	}
}