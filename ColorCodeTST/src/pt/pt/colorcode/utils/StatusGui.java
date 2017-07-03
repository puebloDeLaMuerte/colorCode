package pt.pt.colorcode.utils;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;

public class StatusGui extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4435109490179071418L;
	private StatApp applet;

	public StatusGui() {
		applet = new StatApp();
		applet.init();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();
		
		setBounds((int)width/2-150,(int)height/2-50, 300, 100);

		add(applet);
		applet.update(0,"init...");
		
//		setBounds(300,300, 100,300);
		
	    
	    setTitle("status");
	    setResizable(false);
	    //setExtendedState(JFrame.MAXIMIZED_BOTH);
	    setUndecorated(true);
	    setVisible(true);
	    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
	}
	
	public void start() {
		setAlwaysOnTop(true);
		//setUndecorated(true);
		pack();
		setVisible(true);
	}
	
	public void end() {
 	    setVisible(false);
 	    applet.setEnabled(false);
 	    applet.dispose();
	}
	
	public void update(int _type, String _stat) {
		
		applet.update(_type,_stat);
		applet.redraw();
	}
	
	public void update2(String _stat) {
		
		applet.update2(_stat);
		applet.redraw();
	}
	
	public void start2() {
		applet.start2();
	}
	
	public void completed() {
		applet.completed();
		applet.redraw();
	}

}
