package vita;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;


public class Vita extends JFrame implements Runnable{
	
	private Image bufferImage;									// ���� ���۸�
	private Graphics screenGraphic;								// ���� ���۸�
	
	private Image playerImage = new ImageIcon("src/images/player.png").getImage();				// �÷��̾� �̹���
	private Image playerDamagedImage = new ImageIcon("src/images/player_damaged.png").getImage();
	private Image backgroundImage = new ImageIcon("src/images/background.png").getImage();		// ��� �̹���
	private Image startImage = new ImageIcon("src/images/startscreen.png").getImage();			// ����ȭ�� �̹���
	
	private boolean isMainScreen, isGameScreen;
	
	private Audio mainBGM = new Audio("src/audio/mainBGM.wav", true);
	private Audio coughSound = new Audio("src/audio/fcough.wav", false);
	
	private int playerSize = 30;								// �÷��̾� ĳ���� ũ��
	private int frameWidth = 700;								// ȭ�� ����
	private int frameHeight = 600;								// ȭ�� ����
	private int playerX = (frameWidth - playerSize) / 2;		// �÷��̾� ĳ���� X��ǥ
	private int playerY = (frameHeight - playerSize) / 2;		// �÷��̾� ĳ���� Y��ǥ
	
	private boolean up, down, left, right;						// ����Ű �����ִ��� ����
	private boolean running = false;							// ������� (�ʱⰪ false)
	private boolean isDamaged;
	
    ArrayList<Virus> virus = new ArrayList<Virus>();			// ���̷��� ��ü ������� �迭
    ArrayList<Item> item = new ArrayList<Item>();
    
    private int time;											// �ð�
    
    Hp hp = new Hp();											// HP�� ��ü ����
    
    Thread th;													// ������
	
	public Vita() {
		setTitle("Dodge Virus!");								// ���� ����
		setVisible(true);										// ȭ�� ǥ��
		setSize(frameWidth, frameHeight);						// ȭ�� ũ��
		setLocationRelativeTo(null);							// ȭ�� �߾ӿ� ��ġ
		setResizable(false);									// ũ�� ���� �Ұ�
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);			// �ݱ� ��ư���� ����
		isMainScreen = true;
		init();													// ���̷��� �ʱ� ��ġ
		
		addKeyListener(new KeyAdapter() {						// Ű���� ������
			public void keyPressed(KeyEvent e) {
				switch(e.getKeyCode()) {
				case KeyEvent.VK_UP:							// �� ��ư�� ������
					up = true;									// up������ ���� �������� ����
					break;
				case KeyEvent.VK_DOWN:
					down = true;
					break;
				case KeyEvent.VK_LEFT:
					left = true;
					break;
				case KeyEvent.VK_RIGHT:
					right = true;
					break;
				case KeyEvent.VK_ENTER:							// ���� ������ ������ ����
					start();
					break;
				}
			}													// Ű���� ������
			public void keyReleased(KeyEvent e) {
				switch(e.getKeyCode()) {
				case KeyEvent.VK_UP:
					up = false;
					break;
				case KeyEvent.VK_DOWN:
					down = false;
					break;
				case KeyEvent.VK_LEFT:
					left = false;
					break;
				case KeyEvent.VK_RIGHT:
					right = false;
					break;
				}
			}
		});
	}
	
	public void run() {											// ������ ����
		mainBGM.start();
		while (running) {										// �����ϴ� ���� ��� �ݺ�
			try { 
				Thread.sleep(20);								// 20ms ��ٸ���
			} catch(InterruptedException e) {return;}
			time += 30; 
			KeyProcess();										// Ű �۵�
			moveVirus();										// ���̷��� ����
			checkDamage(); 										// �浹 ����
			checkItem();										// �ð�(ms����) ����
			if(time % 9990 == 0) {
				Item i = new Item();
				item.add(i);
			}
			if (hp.isDied()) {									// hp�� 0�� �Ǹ� ���� ����
				running = false;
				mainBGM.stop();
				coughSound.start();
			}
		}
	}
	
	public void init() {										// ���̷��� �ʱ� ��ġ �޼ҵ�
        Random rnd = new Random(1234);							// ���� �� ����
        int tx, ty;												// ���̷��� ��ġ
        Virus b;												// ���̷��� ��ü 
        
        for (int i = 0; i < 10; i++) {							// 10���� ����
            tx = rnd.nextInt(frameWidth);						// ȭ�� �� �ƹ����̳�
            ty = 0;

            b = new Virus(tx, ty);								// ���̷��� ��ü b ����
            b.setMoveValue(playerX, playerY);					// ���̷����� �÷��̾�� ���ư����� ����

            virus.add(b);										// virus �迭�� ���̷��� ��ü b�� ����
            
            tx = frameWidth;									// ȭ�� ������
            ty = rnd.nextInt(frameHeight);

            b = new Virus(tx, ty);
            b.setMoveValue(playerX, playerY);

            virus.add(b);
            
            tx = rnd.nextInt(frameWidth);						// ȭ�� �Ʒ�
            ty = frameHeight;

            b = new Virus(tx, ty);
            b.setMoveValue(playerX, playerY);

            virus.add(b);
            
            tx = 0;												// ȭ�� ����
            ty = rnd.nextInt(frameHeight);

            b = new Virus(tx, ty);
            b.setMoveValue(playerX, playerY);

            virus.add(b);
        }
	}
	
	public void start() {										// ������ ���� ����
		isMainScreen = false;
		isGameScreen = true;
		if (!running) {
		th = new Thread(this);
		th.start();
		
		playerX = (frameWidth - playerSize) / 2;				// �÷��̾� ��ġ �ʱⰪ
		playerY = (frameHeight - playerSize) / 2;
		time = 0;												// �ð� �ʱⰪ
		hp.setHp();												// hpŬ������ �÷��̾�HP �ʱⰪ �����޼ҵ�
		for(Virus v: virus)
			v.resetPosition();									// ��� ���̷��� ��ü�� �ʱ� ��ġ ���� �޼ҵ�
		running = true;											// ����
		}
	}
	
	public void KeyProcess() {									// Ű���� ó�� �޼ҵ�
		if(up && playerY > 30)									// ȭ�麸�� ���� �� �ö󰡰�
			playerY -= 3;										// �÷��̾� ���� �̵�
		if(down && playerY + playerSize / 2 + 40 < frameHeight) // ȭ�麸�� �Ʒ��� �� ������
			playerY += 3;
		if(left && playerX - 10> 0)								// ���� �� ������
			playerX -= 3;
		if(right && playerX + playerSize / 2 + 20< frameWidth)  // ������ �� ������
			playerX += 3;
	}
	
	public void moveVirus() {									// ȭ�� ������ ���� ���̷������� �ٽ� ���ƿ���
        for (Virus b : virus) {
            if (b.moveVirus(frameWidth, frameHeight) == false)	
                b.setMoveValue(playerX, playerY);
        }
    }
	
	public void checkDamage() {									// �浹 ���� ����
		isDamaged = false;
		for(Virus b: virus) {
			if (b.checkCollision(playerX, playerY, playerSize) == true) {
				hp.damaged();
				isDamaged = true;
			}
		}
	}
	
	public void checkItem() {
		for(int i = 0; i < item.size(); i++) {
			Item it = item.get(i);
			if(it.checkCollision(playerX, playerY, playerSize)) {
				hp.healed();
				item.remove(it);
			}
		}
	}
	
	public void paint(Graphics g) {								// �Ʒ� �͵� ��� �׸���
		bufferImage = createImage(frameWidth, frameHeight);
		screenGraphic = bufferImage.getGraphics();
		screenDraw(screenGraphic);
		if(isGameScreen) {
			Draw();
		}
		g.drawImage(bufferImage, 0, 0, null);
	}
	
	public void screenDraw(Graphics g) {						// ���, �÷��̾�, �ð� �׸���
		if(isMainScreen) {
			g.drawImage(startImage, 0, 0, frameWidth, frameHeight, null);
		}
		if(isGameScreen) {
			g.drawImage(backgroundImage, 0, 0, frameWidth, frameHeight, null);
			if(isDamaged) {
				g.drawImage(playerDamagedImage, playerX, playerY, playerSize, playerSize, null);
			}
			else {
				g.drawImage(playerImage, playerX, playerY, playerSize, playerSize, null);
			}
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", Font.BOLD, 30));
			g.drawString((time / 1000) + " sec", 20 , 60);
		}
		this.repaint();
	}
	
	public void Draw(){											// ���̷���, hp �׸���
        for (Virus b : virus) {
            b.Draw(screenGraphic);
        }
		hp.Draw(screenGraphic);
		for(Item i : item) {
			i.Draw(screenGraphic);
		}
		this.repaint();
	}
	
	public static void main(String[] args) {					// �����Լ�
		new Vita();
	}
}