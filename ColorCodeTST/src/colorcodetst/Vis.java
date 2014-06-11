package colorcodetst;

import processing.core.*;
import sun.tools.tree.CaseStatement;

import javax.swing.JFrame;
import java.awt.MouseInfo;
import javax.swing.*;

public class Vis extends PApplet {
	
	private static final long serialVersionUID = 8530872343427939603L;
	
	private JFrame frame;
	private ColorCodeTST pa;
	private int mouseXOffset, mouseYOffset;
	private int bg = 255;
	
	private boolean visBool = false;
	PImage brush;
	private PGraphics vis;
	private PGraphics show;

	
	public void setJFrame( JFrame _frame) {
		frame = _frame;
	}
	
	public void setParent( ColorCodeTST _parent) {
		pa = _parent;
	}
	
	public void sayHello() {
		System.out.println("Hello, this is the VisApplet "+this.toString());
		bg  =  0;
	}
	
	public void setup() {
		frameRate(6);
		System.out.println("setup run");
		System.out.println("getting brush at " + pa.dataFolderPath+"brush1.png");
		brush = loadImage(pa.dataFolderPath+"brush1.png");
		if(brush == null) { System.err.println("NO BRUSH"); exit(); }
		 addMouseWheelListener(new java.awt.event.MouseWheelListener() {
			 public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
			 mouseWheel(evt.getWheelRotation());
			 }}); 
		noLoop();
	}
	
	void visualize(RelationTable _table, int visMode) {
		switch( visMode ) {
		case 1: visualize1(_table);
		break;
		case 2: visualize2(_table);
		break;
		}
		redraw();
	}
	
	void visualize1(RelationTable _table) {
		

		int cellSize = 12;
		int noOfCells = _table.getSize();
		int xySize = 200 + ( cellSize * noOfCells );

		vis = createGraphics(xySize, xySize);
		
		vis.beginDraw();
		vis.background(255);
		vis.noStroke();

		for(int x = 0; x<noOfCells; x++ ) {
			for(int y = 0; y<noOfCells; y++) {
				
				vis.fill(  255-(_table.getRelationByIndex(x, y)*10)  );
				vis.rect(100+(x*(cellSize)), 100+(y*(cellSize)), cellSize, cellSize);
			}
		}
		
		
		vis.endDraw();
		show = vis;
		visBool = !visBool;
	}
	
	void visualize2(RelationTable _table) {
		

		int cellSize = 12;
		int noOfCells = _table.getSize();
		int xySize = 200 + ( cellSize * noOfCells );

		vis = createGraphics(xySize, xySize);
		brush = loadImage(pa.dataFolderPath+"brush1.png");
		
		vis.beginDraw();
		vis.background(255);
		vis.noStroke();

		//vis.image(brush,200,200);
		
		for(int x = 0; x<noOfCells; x++ ) {
			for(int y = 0; y<noOfCells; y++) {
				
				//vis.fill(  230-(_table.getRelationByIndex(x, y)*10)  );
				//vis.rect(100+(x*(cellSize)), 100+(y*(cellSize)), cellSize, cellSize);
				vis.tint(255, _table.getRelationByIndex(x, y)*2 );
				vis.image(brush,40+(x*(cellSize)), 40+(y*(cellSize)));
				
			}
		}
		
		
		vis.endDraw();
		show = vis;
		visBool = !visBool;
	}
	
	public void saveVisualisation() {
		if(vis != null) {
			System.out.println("SICHER IST SICHER");
			String input =  JOptionPane.showInputDialog(this ,"enter filename:");
			vis.save(pa.dataFolderPath+input+".jpg");
			System.out.println("SAVED!");
		}
		else {
			System.err.println("NO VISUALISATION TO SAVE");
		}
		
		
	}
	
	public void draw() {
		
		if( show == null ) {
			background(0);
			fill(255);
			text("visualizing...", 100, 100);
		}
		else 
		{	
			image(show,0,0,width, height);
		}
	}
	
	public void mousePressed() {
		mouseXOffset = mouseX;
		mouseYOffset = mouseY;
	}
	
	public void mouseDragged() {
		
		int newX = MouseInfo.getPointerInfo().getLocation().x - mouseXOffset;
		int newY = MouseInfo.getPointerInfo().getLocation().y - mouseYOffset;
		
		frame.setLocation( newX, newY);
		
	}

	public void mouseWheel(int delta) {
		System.err.println("MOUSSE");
	}
}


