package colorcodetst;

import processing.core.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.Dimension;
import java.awt.MouseInfo;
import javax.swing.*;

import colorcodetst.ColorCodeTST.tables;

import com.sun.source.tree.CaseTree;

import MyUtils.StatusGui;


public class Vis extends PApplet {
	
	private static final long serialVersionUID = 8530872343427939603L;
	
	private JFrame frame;
	private ColorCodeTST pa;
	private int mouseXOffset, mouseYOffset;
	private int bg = 255;
	private int margin = 200;
	private int cellSize = 20;
	
	private boolean visBool = false;
	private int myVismode = -1;
	private tables myTablesType;
	PImage brush;
	private PGraphics vis;
	//private PGraphics show;
	
	PFont fnt;

	
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
		
		

		vis = createGraphics(100, 100);

		fnt = createFont("Perfect DOS VGA 437.ttf", cellSize-2, true);
		if( fnt == null ) {
			System.err.println("pFont creation failed. exiting...");
			exit();
		} else {
			System.err.println("pFont successfully created");
			vis.textFont(fnt);
		}
		
		brush = loadImage(pa.dataFolderPath+"brush1.png");
		if(brush == null) { System.err.println("NO BRUSH"); exit(); }
		
		 addMouseWheelListener(new java.awt.event.MouseWheelListener() {
			 public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
			 mouseWheel(evt.getWheelRotation());
			 }}); 
		noLoop();
	}
	
	public String getVismodeString() {
		
		switch (myVismode) {
		case -1 : return "NoVisMode";
		case  1 : return "GRID-HARD";
		case  2 : return "GRID-SOFT";
		case  3 : return "CIRCULAR";
		default : return null;
		}
	}
	
	public tables getVisMode() {
		
		return tables.NONE;
	}
	
	public tables getCurrentTableType() {
		
		return myTablesType;
	}
	
 	public void visualize(RelationTable _table, int visMode) {
		
 		visBool = false;
 		redraw();
 		
 		myTablesType = _table.getTablesType();
		myVismode = visMode;
		
		switch( visMode ) {
		case 1: visualize1(_table);
		break;
		case 2: visualize2(_table);
		break;
		case 3: visualizeCircular(_table);
		break;
		}
		redraw();
	}
	
	private void visualize1(RelationTable _table) {
		
		//int maxTint = pa.keywordsList.length;


		int noOfCells = _table.getRowSize();
		int maxTint = _table.getMaxValueTotal();

		int xySize = margin + ( cellSize * noOfCells );

		vis = createGraphics(100, 100);
		fnt = createFont("Perfect DOS VGA 437.ttf", cellSize-2, true);

		vis.setSize(xySize, xySize);
		vis.textFont(fnt);


		
		vis.beginDraw();
		vis.background(bg);
		vis.noStroke();

		vis.pushMatrix();
		vis.fill(0);
		vis.rotate(HALF_PI/2);
		vis.text( _table.getFocal(), 20,0);
		vis.popMatrix();
		
		String term;

		for(int x = 0; x<noOfCells; x++ ) {
			
			term = _table.getTermByIndex("column", x);
			if(term.length()>8) term = term.substring(0, 8);
			
			vis.pushMatrix();
			vis.translate((margin/2)+2+(x*cellSize), 10);
			vis.rotate(HALF_PI);
			vis.fill(0);
			vis.text(term, 0,0);
			vis.popMatrix();
			
			
			for(int y = 0; y<noOfCells; y++) {
				
				
				if(x==0) {
					term = _table.getTermByIndex("row", y);
					vis.fill(0);
					vis.text(term, 10, (margin/2)+(cellSize-2)+(y*cellSize));
				}
				
				vis.fill(  map(_table.getRelationByIndex(y, x), 0, maxTint, 255, 0)  );
				vis.rect((margin/2)+(x*(cellSize)), (margin/2)+(y*(cellSize)), cellSize, cellSize);
			}
		}
		
		
		vis.endDraw();
		//show = vis;
		visBool = true;
	}
	
	private void visualize2(RelationTable _table) {
		

		
		int cellSize = 12;
		int noOfCells = _table.getRowSize();
		int xySize = 200 + ( cellSize * noOfCells );

		vis = createGraphics(xySize, xySize);
		brush = loadImage(pa.dataFolderPath+"brush1.png");
		
		vis.beginDraw();
		vis.background(bg);
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
		//show = vis;
		visBool = true;
	}
	
	private void visualizeCircular(RelationTable _table) {
		pa.stat = new StatusGui();
		pa.stat.update(0, "generating one hell of a visualisation");
		
		int xySize = 1200;		// the size of the final image
		int circOffset = 200;  // how much border-space for the text
		int tableSize = _table.getRowSize();
		float rotaFactor = TWO_PI / tableSize;   // angle between items on circle in radians
		float[][] coordinates = new float[tableSize][2];

		fnt = createFont("Perfect DOS VGA 437.ttf", 2, true);
		vis.textFont(fnt);

		vis = createGraphics(xySize, xySize);
		vis.beginDraw();
		
		vis.background(255);
		vis.ellipse(xySize/2, xySize/2, xySize-circOffset, xySize-circOffset);
		
		vis.pushMatrix();
		vis.translate(xySize/2, xySize/2);
		
		pa.stat.update2("preparing...");
		
		// drawing circle, small indxical lines and legend-txt
		// as well as calculating the XY coordinates for each item
		for( int i = 0; i < tableSize; i++ ) {
			
			vis.line(xySize/2-(circOffset/2),0,xySize/2-((circOffset/2)-22),0);
			vis.fill(0);
			vis.text( _table.getTermByIndex("row", i)  ,xySize/2-((circOffset/2)-10), -4);
			vis.rotate(rotaFactor);
			
			coordinates[i][0] = (xySize/2 ) + (cos(i*rotaFactor) * ((xySize/2)-(circOffset/2)));
			coordinates[i][1] = (xySize/2 )+ (sin(i*rotaFactor) * ((xySize/2)-(circOffset/2)));
			
		}
		vis.popMatrix();

		
		// drawing the actual circular data
		
		float maxRel = _table.getMaxValueTotal();
		float rel;

		for( int i = 0; i < tableSize; i++ ) {
			
			pa.stat.update2("painting... " + i+"/" +tableSize);
			
			vis.noFill();
			PVector ctrlA, ctrlB;
			PVector mid = new PVector(xySize/2, xySize/2);

			for(int ii = 0; ii < tableSize; ii++) {
				
				rel = _table.getRelationByIndex(i, ii);
				if( rel > 0) {
					
					float tint = (25) * (rel / maxRel);
					//float tint = map(rel*(tableSize), 0, maxRel * (tableSize*tableSize), 0, 255);
					vis.stroke(0, tint );
					
					float bow = new Float(0.5);
					
					ctrlA = new PVector(coordinates[i][0], coordinates[i][1]);
					ctrlA.sub(mid);					
					ctrlA.mult(bow);
					ctrlA.add(mid);
					
					ctrlB = new PVector(coordinates[ii][0], coordinates[ii][1]);
					ctrlB.sub(mid);
					ctrlB.mult(bow);
					ctrlB.add(mid);
					
					vis.bezier(coordinates[i][0], coordinates[i][1], ctrlA.x, ctrlA.y, ctrlB.x, ctrlB.y, coordinates[ii][0], coordinates[ii][1]);
				}
			}
		}
		
		
		
		vis.endDraw();
				
		
		visBool = true;
		pa.stat.completed();
		pa.stat.end();
	}
	
	public void saveVisualisation(String _manualName) {
		if(vis != null) {
			//System.out.println("SICHER IST SICHER");
			
			String input = JOptionPane.showInputDialog("please input filename", _manualName);
			
			if(input != null) {
				vis.save(pa.dataFolderPath+input+".jpg");
				System.out.println("SAVED AS: "+pa.dataFolderPath+input+".jpg");
			}
		}
		else {
			System.err.println("NO VISUALISATION TO SAVE");
		}
		
		
	}
	
	public void draw() {
		
		if( !visBool ) {
			background(0);
			fill(255);
			text("visualizing...", 100, 100);
		}
		else if ( vis != null )
		{	
			image(vis,0,0,width, height);
		}
		else System.err.println("vis seems to be NULL...");
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

		Dimension current = new Dimension();
		frame.getSize(current);
		delta *= -2.3;
		current.setSize(current.width+delta, current.height+delta);
		frame.setSize(current);
	}
}


