
import javax.swing.JFrame;

public class MusicBox {
	
	public static void main(String[] args) {
		
		JFrame frame = new JFrame("Music Box");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		MusicBoxControls controlPanel = new MusicBoxControls();
		
		frame.getContentPane().add(controlPanel);
		frame.pack();
		frame.setVisible(true);
	}

}
