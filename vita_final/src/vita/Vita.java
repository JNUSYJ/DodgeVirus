package vita;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;


public class Vita extends JFrame implements Runnable{
	
	private Image bufferImage;									// 이중 버퍼링
	private Graphics screenGraphic;								// 이중 버퍼링
	
	private Image playerImage = new ImageIcon("src/images/player.png").getImage();				// 플레이어 이미지
	private Image playerDamagedImage = new ImageIcon("src/images/player_damaged.png").getImage();
	private Image backgroundImage = new ImageIcon("src/images/background.png").getImage();		// 배경 이미지
	private Image startImage = new ImageIcon("src/images/startscreen.png").getImage();			// 시작화면 이미지
	
	private boolean isMainScreen, isGameScreen;
	
	private Audio mainBGM = new Audio("src/audio/mainBGM.wav", true);
	private Audio coughSound = new Audio("src/audio/fcough.wav", false);
	
	private int playerSize = 30;								// 플레이어 캐릭터 크기
	private int frameWidth = 700;								// 화면 넓이
	private int frameHeight = 600;								// 화면 높이
	private int playerX = (frameWidth - playerSize) / 2;		// 플레이어 캐릭터 X좌표
	private int playerY = (frameHeight - playerSize) / 2;		// 플레이어 캐릭터 Y좌표
	
	private boolean up, down, left, right;						// 방향키 눌려있는지 여부
	private boolean running = false;							// 실행상태 (초기값 false)
	private boolean isDamaged;
	
    ArrayList<Virus> virus = new ArrayList<Virus>();			// 바이러스 객체 집어넣을 배열
    ArrayList<Item> item = new ArrayList<Item>();
    
    private int time;											// 시간
    
    Hp hp = new Hp();											// HP바 객체 생성
    
    Thread th;													// 스레드
	
	public Vita() {
		setTitle("Dodge Virus!");								// 게임 제목
		setVisible(true);										// 화면 표시
		setSize(frameWidth, frameHeight);						// 화면 크기
		setLocationRelativeTo(null);							// 화면 중앙에 위치
		setResizable(false);									// 크기 조정 불가
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);			// 닫기 버튼으로 종료
		isMainScreen = true;
		init();													// 바이러스 초기 배치
		
		addKeyListener(new KeyAdapter() {						// 키보드 리스너
			public void keyPressed(KeyEvent e) {
				switch(e.getKeyCode()) {
				case KeyEvent.VK_UP:							// 위 버튼을 누르면
					up = true;									// up변수에 누른 상태임을 전달
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
				case KeyEvent.VK_ENTER:							// 엔터 누르면 스레드 시작
					start();
					break;
				}
			}													// 키보드 리스너
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
	
	public void run() {											// 스레드 내용
		mainBGM.start();
		while (running) {										// 실행하는 동안 계속 반복
			try { 
				Thread.sleep(20);								// 20ms 기다리기
			} catch(InterruptedException e) {return;}
			time += 30; 
			KeyProcess();										// 키 작동
			moveVirus();										// 바이러스 구현
			checkDamage(); 										// 충돌 구현
			checkItem();										// 시간(ms단위) 증가
			if(time % 9990 == 0) {
				Item i = new Item();
				item.add(i);
			}
			if (hp.isDied()) {									// hp가 0이 되면 실행 종료
				running = false;
				mainBGM.stop();
				coughSound.start();
			}
		}
	}
	
	public void init() {										// 바이러스 초기 배치 메소드
        Random rnd = new Random(1234);							// 랜덤 값 추출
        int tx, ty;												// 바이러스 위치
        Virus b;												// 바이러스 객체 
        
        for (int i = 0; i < 10; i++) {							// 10개씩 생성
            tx = rnd.nextInt(frameWidth);						// 화면 위 아무곳이나
            ty = 0;

            b = new Virus(tx, ty);								// 바이러스 객체 b 생성
            b.setMoveValue(playerX, playerY);					// 바이러스가 플레이어에게 날아가도록 설정

            virus.add(b);										// virus 배열에 바이러스 객체 b를 넣음
            
            tx = frameWidth;									// 화면 오른쪽
            ty = rnd.nextInt(frameHeight);

            b = new Virus(tx, ty);
            b.setMoveValue(playerX, playerY);

            virus.add(b);
            
            tx = rnd.nextInt(frameWidth);						// 화면 아래
            ty = frameHeight;

            b = new Virus(tx, ty);
            b.setMoveValue(playerX, playerY);

            virus.add(b);
            
            tx = 0;												// 화면 왼쪽
            ty = rnd.nextInt(frameHeight);

            b = new Virus(tx, ty);
            b.setMoveValue(playerX, playerY);

            virus.add(b);
        }
	}
	
	public void start() {										// 스레드 시작 수현
		isMainScreen = false;
		isGameScreen = true;
		if (!running) {
		th = new Thread(this);
		th.start();
		
		playerX = (frameWidth - playerSize) / 2;				// 플레이어 위치 초기값
		playerY = (frameHeight - playerSize) / 2;
		time = 0;												// 시간 초기값
		hp.setHp();												// hp클래스의 플레이어HP 초기값 설정메소드
		for(Virus v: virus)
			v.resetPosition();									// 모든 바이러스 객체들 초기 위치 설정 메소드
		running = true;											// 실행
		}
	}
	
	public void KeyProcess() {									// 키보드 처리 메소드
		if(up && playerY > 30)									// 화면보다 위로 못 올라가게
			playerY -= 3;										// 플레이어 위로 이동
		if(down && playerY + playerSize / 2 + 40 < frameHeight) // 화면보다 아래로 못 나가게
			playerY += 3;
		if(left && playerX - 10> 0)								// 왼쪽 못 나가게
			playerX -= 3;
		if(right && playerX + playerSize / 2 + 20< frameWidth)  // 오른쪽 못 나가게
			playerX += 3;
	}
	
	public void moveVirus() {									// 화면 밖으로 나간 바이러스들을 다시 돌아오게
        for (Virus b : virus) {
            if (b.moveVirus(frameWidth, frameHeight) == false)	
                b.setMoveValue(playerX, playerY);
        }
    }
	
	public void checkDamage() {									// 충돌 여부 판정
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
	
	public void paint(Graphics g) {								// 아래 것들 모두 그리기
		bufferImage = createImage(frameWidth, frameHeight);
		screenGraphic = bufferImage.getGraphics();
		screenDraw(screenGraphic);
		if(isGameScreen) {
			Draw();
		}
		g.drawImage(bufferImage, 0, 0, null);
	}
	
	public void screenDraw(Graphics g) {						// 배경, 플레이어, 시계 그리기
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
	
	public void Draw(){											// 바이러스, hp 그리기
        for (Virus b : virus) {
            b.Draw(screenGraphic);
        }
		hp.Draw(screenGraphic);
		for(Item i : item) {
			i.Draw(screenGraphic);
		}
		this.repaint();
	}
	
	public static void main(String[] args) {					// 메인함수
		new Vita();
	}
}