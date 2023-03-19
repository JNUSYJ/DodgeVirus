package vita;

import javax.sound.sampled.*;
import java.io.*;

public class Audio {
	private Clip clip;
	private File audioFile;
	private AudioInputStream audioInputStream;
	private boolean isLoop;
	
	public Audio(String pathName, boolean isLoop) {
		try {
			this.isLoop = isLoop;
			clip = AudioSystem.getClip();
			audioFile = new File(pathName);
			audioInputStream = AudioSystem.getAudioInputStream(audioFile);
			clip.open(audioInputStream);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	}
	
	public void start() {
		clip.setFramePosition(0);
		clip.start();
		if(isLoop) clip.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	public void stop() {
		clip.stop();
	}
}
