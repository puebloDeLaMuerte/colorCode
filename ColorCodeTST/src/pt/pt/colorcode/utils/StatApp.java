package pt.pt.colorcode.utils;

import processing.core.PApplet;

public class StatApp extends PApplet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8467899174202348599L;

	private boolean debug = true;

	private String status;
	private String detail;
	private int type;
	
	public void setup() {
		
		dprint("hello Applet");
		
		//size(600, 200);
		//noLoop();
		fill(0,255,0);
		
		background(0,255,0);
		
		
	}
	
//	public int getWidth() {
//		return this.width;
//	}
//	
//	public int getHeight() {
//		return this.height;
//	}
	
	public void draw() {
		
		background(0);
		
		for(int i =0; i< this.height; i++) {
			stroke((float)random(60),(float)random(40),(float)random(60));
			line(0,i,width,i);
		}
		
		if( type == 0 ) fill(255);
		if( type == 1 ) fill(0,255,0);
		//else fill(255,0,0);
		text(status, 20,40);
		if(detail != null) text(detail, 20,60);
	}
	
	public void update(int _type, String _stat){
		status = _stat;
		type = _type;

	}
	
	public void update2(String _stat){
		detail = _stat;
	}
	
	public void completed() {
		detail = null;
		update(1,"completed");
	}
	
	private void dprint(String _p) {
		if( debug ) System.err.println(_p);
	}

	public void start2() {
		detail = "";
	}

}
