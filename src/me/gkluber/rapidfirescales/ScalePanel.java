package me.gkluber.rapidfirescales;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

public class ScalePanel extends JPanel implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6239866885457081838L;
	
	private FlowLayout layout;
	private final String[] keys = new String[] {
			"CM","GM","DM","AM","EM","BM","F#M","C#M", //sharp keys
			"FM","BbM","EbM","AbM","DbM","GbM","CbM" //flat keys
	};
	
	private final Set<String> enabled;
	private Main parent;
	private int width, height; //internal variables to ensure consistency with JFrame
	private final JButton[] scaleButtons = new JButton[keys.length];
	private String currentScale;
	private Random r = new Random();
	private float baseSaturation = 0.1f;
	private float baseBrightness = 0.95f;
	private Font scaleFont;
	private Font subtitle;
	private final String subtitleText;
	
	private Timer updater;
	private AtomicLong frameNumber;
	
	public ScalePanel(Main parent)
	{
		super();
		this.parent = parent;
		
		layout = (FlowLayout) this.getLayout();
		layout.setVgap(8*Main.HEIGHT/10);
		
		//for adjusting the dynamic background
		frameNumber = new AtomicLong(0);
		updater = new Timer(20, this);
		updater.start();
		
		this.setFocusable(true);
		this.setOpaque(true);
		
		
		enabled = new HashSet<>(keys.length);
		
		//fonts and text
		scaleFont = new Font(Font.DIALOG, Font.BOLD, 150);
		subtitle = new Font(Font.SANS_SERIF, Font.ITALIC, 30);
		subtitleText = "Press space to draw a scale!";
		
		//lower buttons
		
		for(int i=0; i<keys.length; i++)
		{
			JButton button = new JButton(keys[i]);
			stylizeButton(button);
			//button.setBounds(50 + Main.WIDTH*i/keys.length, Main.HEIGHT - 50, 50, 30);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					Object src = e.getSource();
					if(!(src instanceof JButton))
						return;
					JButton srcButton = (JButton) src;
					String key = srcButton.getText();
					
					if(enabled.contains(key))
					{
						enabled.remove(key);
						srcButton.setBackground(Color.WHITE);
					}
						
					else
					{
						enabled.add(key);
						srcButton.setBackground(Color.GREEN);
					}
				}
			});
			
			this.add(button);
			scaleButtons[i] = button;
		}
		
		JButton all = new JButton("All");
		stylizeButton(all);
		all.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				Object src = e.getSource();
				if(!(src instanceof JButton))
					return;
				if(enabled.size()>0)
				{
					enabled.clear();
					for(JButton scale : scaleButtons)
						scale.setBackground(Color.WHITE);
				}
					
				else
				{
					for(String key : keys)
						enabled.add(key);
					for(JButton scale : scaleButtons)
						scale.setBackground(Color.GREEN);
				}
				
			}
		});
		
		this.add(all);
		
		this.currentScale = " ";
	}
	
	public void drawNextScale()
	{
		if(enabled.size()==0)
			return;
		int random = r.nextInt(enabled.size());
		currentScale = enabled.toArray(new String[enabled.size()])[random]; // randomly draw scale
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		
		//updates
		super.paintComponent(g);
		if(parent.getWidth()!=width || parent.getHeight() != height)
			this.setSize(parent.getWidth(), parent.getHeight());
		
		//dynamic background
		System.out.println(getColor().toString());
		g2d.setColor(this.getColor());
		g2d.fillRect(0, 0, width, height);
		
		//draw current scale centered
		g2d.setColor(Color.BLACK);
		FontMetrics metrics = g2d.getFontMetrics(scaleFont);
		int x = width/2 - metrics.stringWidth(currentScale) / 2;
		int y = height/2 - metrics.getHeight()/2 + metrics.getAscent();
		g2d.setFont(scaleFont);
		g2d.drawString(currentScale, x, y);
		
		//draw info at top
		metrics = g2d.getFontMetrics(subtitle);
		x = width/2 - metrics.stringWidth(subtitleText) / 2;
		y = height/20 - metrics.getHeight()/2 + metrics.getAscent(); 
		g2d.setFont(subtitle);
		g2d.drawString(subtitleText, x, y);
	}
	
	public void stylizeButton(JButton button)
	{
		button.setOpaque(true);
		button.setPreferredSize(new Dimension(70,50));
		button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
		button.setBackground(Color.WHITE);
		button.setFocusable(false);
	}
	
	@Override
	public void setSize(int width, int height)
	{
		this.width = width;
		this.height = height;
		layout.setVgap(8*height/10);
	}

	@Override
	public void processKeyEvent(KeyEvent e) {
		super.processKeyEvent(e);
		int keycode = e.getKeyCode();
		if(keycode == KeyStroke.getKeyStroke(' ').getKeyCode()) //space
			drawNextScale();
	}

	//receives calls from the Timer swing object
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==updater)
		{
			frameNumber.getAndIncrement();
			this.repaint();
		}
	}
	
	public Color getColor()
	{
		System.out.println(frameNumber.get());
		return Color.getHSBColor((float)frameNumber.get()/360f % 1f, this.baseSaturation, this.baseBrightness);
	}
}
