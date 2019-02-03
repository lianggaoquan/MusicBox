import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 控制面板
 * @author lenov
 *
 */

public class MusicBoxControls extends JPanel {
	// 组合框
	private JComboBox musicCombo;
	private JButton stopButton, playButton;
	private JButton forwardButton, backwardButton;
	private AudioClip[] music;
	private AudioClip current;
	private JTextField query;		// song query
	private JLabel inputLabel;
	private String[] musicNames = null;
	private HashMap<String,Integer> songMap = null;
	private String[] selectedMusicNames = null;		// for search engine
	private static HashMap<String,Integer> wordMap = null; // word map
	
	public MusicBoxControls() {
		
		String rootDir = "D:/music";
		File musicDir = new File(rootDir);
		URL[] urls = null;
		int i = 0;
		int hashVal = 0;
		songMap = new HashMap<String,Integer>();
		wordMap = new HashMap<String,Integer>();
		
		if(musicDir.isDirectory()) {
			String[] ms = musicDir.list();
			urls = new URL[ms.length];
			musicNames = new String[ms.length+1];
			musicNames[0] = "Make a selection";
			
			for(i=0;i<ms.length;i++) {
				File m = new File(rootDir+"/"+ms[i]);
				// assume all the files in the dir is wav files
				musicNames[i+1] = m.getName().split("\\.")[0];
				songMap.put(musicNames[i+1], i+1);
				
				for(String s:musicNames[i+1].split("")) {
					if(!wordMap.containsKey(s)) {
						wordMap.put(s, hashVal++);
					}
				}

				try {
					urls[i] = m.toURI().toURL();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
		
		
		music = new AudioClip[urls.length+1];
		music[0] = null;  // make a selection
		
		for(i=0;i<urls.length;i++) {
			music[i+1] = JApplet.newAudioClip(urls[i]);
		}
		
		JLabel titleLabel = new JLabel("Music Box");
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		inputLabel = new JLabel("Search Music:  ");
		
		query = new JTextField(5);
		query.addActionListener(new TextListener());
		
		musicCombo = new JComboBox(musicNames);
		musicCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		// set up the buttons
		playButton = new JButton("Play");
		playButton.setBackground(Color.white);
		playButton.setMnemonic('p');
		
		stopButton = new JButton("Stop");
		stopButton.setBackground(Color.white);
		stopButton.setMnemonic('s');
		
		forwardButton = new JButton(">");
		forwardButton.setBackground(Color.white);
		
		backwardButton = new JButton("<");
		backwardButton.setBackground(Color.white);
		
		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons,BoxLayout.X_AXIS));
		buttons.add(backwardButton);
		buttons.add(Box.createRigidArea(new Dimension(5,0)));
		buttons.add(playButton);
		buttons.add(Box.createRigidArea(new Dimension(5,0)));
		buttons.add(stopButton);
		buttons.add(Box.createRigidArea(new Dimension(5,0)));
		buttons.add(forwardButton);
		buttons.setBackground(Color.cyan);
		
		setPreferredSize(new Dimension(500,150));
		setBackground(Color.cyan);
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		add(Box.createRigidArea(new Dimension(0,5)));
		add(titleLabel);
		
		JPanel search = new JPanel();
		search.setLayout(new BoxLayout(search,BoxLayout.X_AXIS));
		search.add(inputLabel);
		search.add(query);
		search.setBackground(Color.cyan);
		
		add(Box.createRigidArea(new Dimension(0,5)));
		add(search);
		
		add(Box.createRigidArea(new Dimension(0,5)));
		add(musicCombo);
		
		add(Box.createRigidArea(new Dimension(0,5)));
		add(buttons);
		
		add(Box.createRigidArea(new Dimension(0,5)));
		musicCombo.addActionListener(new ComboListener());
		stopButton.addActionListener(new ButtonListener());
		playButton.addActionListener(new ButtonListener());
		forwardButton.addActionListener(new ButtonListener());
		backwardButton.addActionListener(new ButtonListener());
		
		current = null;
	}
	
	private class ComboListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(current != null) {
				current.stop();
			}
			current = music[musicCombo.getSelectedIndex()];
		}
		
	}
	
	private class ButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(current != null) {
				current.stop();
			}
			
			if(e.getSource() == playButton) {
				if(current != null) {
					current.play();
				}
			}
			
			if(e.getSource() == forwardButton) {
				// musicCombo.getSelectedIndex()+1 must inside 
				// the index range [0..music.length-1], that is .. < music.length
				if (musicCombo.getSelectedIndex()+1 < music.length) {
					
					musicCombo.setSelectedIndex(musicCombo.getSelectedIndex() + 1);
					current = music[musicCombo.getSelectedIndex()];
					
				} else {
					musicCombo.setSelectedIndex(1);
					current = music[musicCombo.getSelectedIndex()];
				}
				
				if (current != null) {
					current.play();
				}
				
			}
			
			if(e.getSource() == backwardButton) {
				if (musicCombo.getSelectedIndex() >= 1) {
					
					musicCombo.setSelectedIndex(musicCombo.getSelectedIndex()-1);
					current = music[musicCombo.getSelectedIndex()];
					
				} else {
					musicCombo.setSelectedIndex(musicNames.length-1);
					current = music[musicCombo.getSelectedIndex()];
				}
				
				if (current != null) {
					current.play();
				}
			}
		}
		
	}
	
	private class TextListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
			Integer[] queryVec = createVec(query.getText());
			Integer[][] songVec = new Integer[songMap.size()][wordMap.size()];
			Integer[] dotprod = new Integer[songMap.size()];
			
			// for every song, compute its vec and dot prod
			for(int j=0;j<songVec.length;j++) {
				songVec[j] = createVec(musicNames[j+1]);
				dotprod[j] = dotProduct(songVec[j],queryVec);
			}
			
			int id = findMaxProdId(dotprod);
			musicCombo.setSelectedIndex(id+1);
			current = music[id+1];
			
			if(current != null) {
				current.play();
			}
		}
		
	}
	
	/**
	 * use bit vector and dot product to compute similarity 
	 * between query and music names.
	 * @param str
	 * @return
	 */
	public static Integer[] createVec(String str) {
		Integer[] vec = new Integer[wordMap.size()];
		// init vec
		for(int i=0;i<vec.length;i++) {
			vec[i] = 0;
		}
		
		for(String s:str.split("")) {
			if (wordMap.containsKey(s)) {
				vec[wordMap.get(s)] = 1;
			}
		}
		
		return vec;
	}
	
	/**
	 * dot product computing
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static int dotProduct(Integer[] v1, Integer[] v2) {
		assert v1.length == v2.length;
		int dotprod = 0;
		
		for(int i=0;i<v1.length;i++) {
			dotprod += v1[i]*v2[i];
		}
		
		return dotprod;
	}
	
	/**
	 * find the max dot product id, which is exactly the song that search
	 * engine returns.
	 * @param arr
	 * @return
	 */
	public static int findMaxProdId(Integer[] arr) {
		int index = 0;
		int max = arr[0];
		for(int i=1;i<arr.length;i++) {
			if(arr[i] > max) {
				max = arr[i];
				index = i;
			}
		}
		return index;
	}
}
