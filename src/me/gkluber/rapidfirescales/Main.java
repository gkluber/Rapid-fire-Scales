package me.gkluber.rapidfirescales;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JFrame;

public class Main extends JFrame {
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	
	public Main()
	{
		ScalePanel panel = new ScalePanel(this);
		this.getContentPane().add(panel);
		this.setTitle("Rapid Fire Scales");
		this.setMinimumSize(new Dimension(400,400));
		this.setSize(1280,720);
		this.setResizable(true);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		this.addComponentListener(new ComponentListener() {
			@Override
		    public void componentResized(ComponentEvent e) {
		        panel.setSize(((Component)e.getSource()).getWidth(), e.getComponent().getHeight());        
		    }

			@Override
			public void componentHidden(ComponentEvent e) {
				panel.setSize(((Component)e.getSource()).getWidth(), e.getComponent().getHeight());        
				
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				panel.setSize(((Component)e.getSource()).getWidth(), e.getComponent().getHeight());        
				
			}

			@Override
			public void componentShown(ComponentEvent e) {
				panel.setSize(((Component)e.getSource()).getWidth(), e.getComponent().getHeight());       
				
			}
		});
	}
	
	public static void main(String[] args) {
		Main m = new Main();
		m.setVisible(true);
	}
}
