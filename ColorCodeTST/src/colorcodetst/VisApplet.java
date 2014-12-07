package colorcodetst;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.sun.tools.javac.comp.Todo;
import com.sun.xml.internal.bind.v2.TODO;

import MyUtils.StatusGui;
import MyUtils.VisModes;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import MyUtils.TableTypes;

public class VisApplet extends PApplet implements VisInterface{
	
	private static final long serialVersionUID = 4722827600944467489L;
	public VisInterface vis;
	private JFrame frame;
	private ColorCodeTST pa;
	private int mouseXOffset, mouseYOffset;
	private PGraphics display;
	
	public void setup() {
		
		size(frame.getWidth(), frame.getHeight());
		
	}
	
	public void draw() {
		if( vis == null || !vis.hasVisual() ) {
			background(0);
			fill(255);
			text("visualizing...", random(width), random(height));
		}
		else if ( display != null )
		{	
			image(display,0,0,width, height);
		}
		else System.err.println("vis seems to be NULL...");
	}
	
	public void mouseWheel(int delta) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean hasVisual() {
		if( vis != null && vis.hasVisual() ) return true;
		else return false;
	}
	
	public void setJFrame(JFrame _frame) {
		frame = _frame;
	}

	public void setParent(ColorCodeTST _parent) {
			pa = _parent;
	}

	@Override
	public void sayHello() {
		if( vis != null ) {
			vis.sayHello();
		}
		else System.out.println("this is the VisApplet. There doesn't seem to be a visInterface in place, sorry!");
		
	}

	@Override
	public String getVismodeString() {
		if( vis != null ) {
			return vis.getVismodeString();
		}
		else return "vis is null";
	}

	public VisModes getVisMode() {
		if( vis != null ) return vis.getVisMode();
		else return null;
	}
	
	@Override
	public MyUtils.TableTypes getCurrentTableType() {
		if( vis != null ) {
			return vis.getCurrentTableType();
		}
		else return null;
	}

	public void setVisMode() {
		
		
	}
	
	@Override
	public void visualize(RelationTable _table) {
		// TODO check the parameters and start the visInterface-based process
	}

	@Override
	public boolean saveVisualisation(boolean _askForName, String _suggestedName) {
		if( vis != null ) {
			return vis.saveVisualisation(_askForName, _suggestedName);
		}
		else return false;
	}	

	
	//||\\||//||\\||//||\\||//||\\||//||\\||//||\\||//||\\||//||\\||//||\\||//||\\||

	
	public class Nebular implements VisInterface {

		private static final long serialVersionUID = 2990147396825116184L;

		private String myVismodeString = "nebular";
		private VisModes myVismode = VisModes.NEBULAR;
		
		public Nebular(){
			
			System.out.println("Neb established");
		}
		
		public void doIt() {
			System.out.println("Neb sayin' hi");
		}
		
		
		private class Element {
			
			private String myTerm, myLove;
			private int myLoveLevel;
			
			public Element( String _myTerm, String _myLove, int _loveLevel) {
				
				myTerm = _myTerm;
				myLove = _myLove;
				myLoveLevel = _loveLevel;
			}
			
			
		}


		@Override
		public void sayHello() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean hasVisual() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public String getVismodeString() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TableTypes getCurrentTableType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void visualize(RelationTable _table) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean saveVisualisation(boolean _askForName,
				String _suggestedName) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public VisModes getVisMode() {
			// TODO Auto-generated method stub
			return null;
		}


		
	}

	public class GridSoft implements VisInterface{
		
		private static final long serialVersionUID = 8530872343427939603L;
		
		
		private int bg = 255;
		private int margin = 200;
		private int cellSize = 20;
		
		private boolean visBool = false;
		private String myVismodeString = "grid soft";
		private VisModes myVismode = VisModes.GRID_SOFT;
		private TableTypes myTablesType;
		PImage brush;
		private PGraphics graphics;
		//private PGraphics show;
		
		PFont fnt;
		
		public void sayHello() {
			System.out.println("Hello, this is the softGridDrawer module "+this.toString());
		}
		
		public void setup() {
			VisApplet.this.frameRate(6);
			System.out.println("setup run");
			System.out.println("getting brush at " + pa.dataFolderPath+"brush1.png");
			
			

			graphics = createGraphics(100, 100);
			String fontpath = pa.dataFolderPath+"Perfect DOS VGA 437.ttf";
			System.out.println("getting font at: " + fontpath);
			
			File rootFolder = new File(".");
			System.out.println(rootFolder.getAbsolutePath());
			
			fnt = createFont(fontpath, cellSize-2, true);

			if( fnt == null ) {
				System.err.println("pFont creation failed. exiting...");
				exit();
			} else {
				System.err.println("pFont successfully created");
				graphics.textFont(fnt);
			}
			
			brush = loadImage(pa.dataFolderPath+"brush1.png");
			if(brush == null) { System.err.println("NO BRUSH"); exit(); }
			
			 VisApplet.this.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
				 public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
				 mouseWheel(evt.getWheelRotation());
				 }}); 
			noLoop();
			VisApplet.this.background(0);
		}
		
		public String getVismodeString() {
			
			return myVismodeString;
		}
				
		public TableTypes getCurrentTableType() {
			
			return myTablesType;
		}
		
	 	public void visualize(RelationTable _table) {
			
	 		visBool = false;
	 		myTablesType = _table.getTablesType();
			
			drawGridSoft(_table);
	 		
			frame.setTitle( _table.getTablesTypeAsString()+" focal: "+_table.getFocal());
			
		}
				
		private void drawGridSoft(RelationTable _table) {
			

			
			int cellSize = 12;
			int noOfCells = _table.getRowSize();
			int xySize = 200 + ( cellSize * noOfCells );

			graphics = createGraphics(xySize, xySize);
			brush = loadImage(pa.dataFolderPath+"brush1.png");
			
			graphics.beginDraw();
			graphics.background(bg);
			graphics.noStroke();

			//vis.image(brush,200,200);
			
			for(int x = 0; x<noOfCells; x++ ) {
				for(int y = 0; y<noOfCells; y++) {
					
					//vis.fill(  230-(_table.getRelationByIndex(x, y)*10)  );
					//vis.rect(100+(x*(cellSize)), 100+(y*(cellSize)), cellSize, cellSize);
					graphics.tint(255, _table.getRelationByIndex(x, y)*2 );
					graphics.image(brush,40+(x*(cellSize)), 40+(y*(cellSize)));
					
				}
			}
			
			
			graphics.endDraw();
			//show = vis;
			visBool = true;
		}
				
	 	public boolean saveVisualisation(boolean _askForName, String _suggestedName) {
			if(graphics != null) {
				//System.out.println("SICHER IST SICHER");
				
				String input;
				if (_askForName) {
					input = JOptionPane.showInputDialog("please input filename", _suggestedName);
				}
				else {
					input = _suggestedName;
				}
				
				if( input == null ) { return false; }
				
				if( input != null ) {
					graphics.save(pa.dataFolderPath+input+".jpg");
					//vis.save(input+".jpg");
					System.out.println("SAVED AS: "+pa.dataFolderPath+input+".jpg");
					
				}
				return true;
			}
			else {
				System.err.println("NO VISUALISATION TO SAVE");
				return false;
			}
			
			
		}
		
		@Override
		public boolean hasVisual() {
			// TODO Auto-generated method stub
			return false;
		}

		public VisModes getVisMode() {
			return myVismode;
		}
		
	}

	public class GridHard implements VisInterface{
		
		private static final long serialVersionUID = 8530872343427939603L;
		
		
		private int bg = 255;
		private int margin = 200;
		private int cellSize = 20;
		
		private boolean visBool = false;
		private String myVismodeString = "gridHard";
		private VisModes myVismode = VisModes.GRID_HARD;
		private TableTypes myTablesType;
		PImage brush;
		private PGraphics graphics;
		//private PGraphics show;
		
		PFont fnt;

		
		public void sayHello() {
			System.out.println("Hello, this is the HardGridDrawer module "+this.toString());
		}
		
		public void initializeParams() {
			
			VisApplet.this.frameRate(6);
			System.out.println("setup run");
			System.out.println("getting brush at " + pa.dataFolderPath+"brush1.png");
			
			

			graphics = createGraphics(100, 100);
			String fontpath = pa.dataFolderPath+"Perfect DOS VGA 437.ttf";
			System.out.println("getting font at: " + fontpath);
			
			File rootFolder = new File(".");
			System.out.println(rootFolder.getAbsolutePath());
			
			fnt = createFont(fontpath, cellSize-2, true);

			if( fnt == null ) {
				System.err.println("pFont creation failed. exiting...");
				exit();
			} else {
				System.err.println("pFont successfully created");
				graphics.textFont(fnt);
			}
			
			brush = loadImage(pa.dataFolderPath+"brush1.png");
			if(brush == null) { System.err.println("NO BRUSH"); exit(); }
			
			 VisApplet.this.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
				 public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
				 mouseWheel(evt.getWheelRotation());
				 }}); 
			noLoop();
			VisApplet.this.background(0);
		}
		
		public String getVismodeString() {
			
			return myVismodeString;
		}
				
		public TableTypes getCurrentTableType() {
			
			return myTablesType;
		}
		
	 	public void visualize(RelationTable _table) {
			
	 		visBool = false;
	 		myTablesType = _table.getTablesType();
			
			drawGridHard(_table);
	 		
			frame.setTitle( _table.getTablesTypeAsString()+" focal: "+_table.getFocal());
			
		}
		
		private void drawGridHard(RelationTable _table) {
			
			//int maxTint = pa.keywordsList.length;


			int noOfCells = _table.getRowSize();
			int maxTint = _table.getMaxValueTotal();

			int xySize = margin + ( cellSize * noOfCells );

			graphics = createGraphics(100, 100);
			fnt = createFont("Perfect DOS VGA 437.ttf", cellSize-2, true);

			graphics.setSize(xySize, xySize);
			graphics.textFont(fnt);


			
			graphics.beginDraw();
			graphics.background(bg);
			graphics.noStroke();

			graphics.pushMatrix();
			graphics.fill(0);
			graphics.rotate(HALF_PI/2);
			graphics.text( _table.getFocal(), 20,0);
			graphics.popMatrix();
			
			String term;

			for(int x = 0; x<noOfCells; x++ ) {
				
				term = _table.getTermByIndex("column", x);
				if(term.length()>8) term = term.substring(0, 8);
				
				graphics.pushMatrix();
				graphics.translate((margin/2)+2+(x*cellSize), 10);
				graphics.rotate(HALF_PI);
				graphics.fill(0);
				graphics.text(term, 0,0);
				graphics.popMatrix();
				
				
				for(int y = 0; y<noOfCells; y++) {
					
					
					if(x==0) {
						term = _table.getTermByIndex("row", y);
						graphics.fill(0);
						graphics.text(term, 10, (margin/2)+(cellSize-2)+(y*cellSize));
					}
					
					graphics.fill(  map(_table.getRelationByIndex(y, x), 0, maxTint, 255, 0)  );
					graphics.rect((margin/2)+(x*(cellSize)), (margin/2)+(y*(cellSize)), cellSize, cellSize);
				}
			}
			
			
			graphics.endDraw();
			//show = vis;
			visBool = true;
		}
		
	 	public boolean saveVisualisation(boolean _askForName, String _suggestedName) {
			if(graphics != null) {
				//System.out.println("SICHER IST SICHER");
				
				String input;
				if (_askForName) {
					input = JOptionPane.showInputDialog("please input filename", _suggestedName);
				}
				else {
					input = _suggestedName;
				}
				
				if( input == null ) { return false; }
				
				if( input != null ) {
					graphics.save(pa.dataFolderPath+input+".jpg");
					//vis.save(input+".jpg");
					System.out.println("SAVED AS: "+pa.dataFolderPath+input+".jpg");
					
				}
				return true;
			}
			else {
				System.err.println("NO VISUALISATION TO SAVE");
				return false;
			}
			
			
		}

	 	@Override
	 	public boolean hasVisual() {
	 		// TODO Auto-generated method stub
	 		return false;
	 	}

	 	@Override
	 	public VisModes getVisMode() {
	 		return myVismode;
	 	}


	}
	
	public class Circular implements VisInterface{
		
		private static final long serialVersionUID = 8530872343427939603L;
				
		private boolean visBool = false;
		private String myVismodeString = "circular";
		private VisModes myVismode = VisModes.CIRCULAR;
		private TableTypes myTablesType;
		PImage brush;
		private PGraphics graphics;
		//private PGraphics show;
		
		PFont fnt;

		public void sayHello() {
			System.out.println("Hello, this is the CircularVisualisationDrawerModule "+this.toString());
		}
		
		public void initializeParams() {
			
			VisApplet.this.frameRate(6);
			System.out.println("setup run");
			System.out.println("getting brush at " + pa.dataFolderPath+"brush1.png");
			
			

			graphics = createGraphics(100, 100);
			String fontpath = pa.dataFolderPath+"Perfect DOS VGA 437.ttf";
			System.out.println("getting font at: " + fontpath);
			
			File rootFolder = new File(".");
			System.out.println(rootFolder.getAbsolutePath());
			
			fnt = createFont(fontpath, 18, true);

			if( fnt == null ) {
				System.err.println("pFont creation failed. exiting...");
				exit();
			} else {
				System.err.println("pFont successfully created");
				graphics.textFont(fnt);
			}
			
			brush = loadImage(pa.dataFolderPath+"brush1.png");
			if(brush == null) { System.err.println("NO BRUSH"); exit(); }
			
			 VisApplet.this.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
				 public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
				 mouseWheel(evt.getWheelRotation());
				 }}); 
			noLoop();
			VisApplet.this.background(0);
		}
		
		public String getVismodeString() {
			
			return myVismodeString;
		}
				
		public TableTypes getCurrentTableType() {
			
			return myTablesType;
		}
		
	 	public void visualize(RelationTable _table) {
			
	 		visBool = false;
	 		myTablesType = _table.getTablesType();
			
	 		drawCircular(_table);
	 		
			frame.setTitle( _table.getTablesTypeAsString()+" focal: "+_table.getFocal());
			visBool = true;
		}
	
		private void drawCircular(RelationTable _table) {
			
			pa.stat = new StatusGui();
			pa.stat.update(0, "generating one hell of a visualisation");
			
			int xySize = 1200;		// the size of the final image
			int circOffset = 200;  // how much border-space for the text
			int tableSize = _table.getRowSize();
			float rotaFactor = TWO_PI / tableSize;   // angle between items on circle in radians
			float[][] coordinates = new float[tableSize][2];

			fnt = createFont("Perfect DOS VGA 437.ttf", 2, true);
			graphics.textFont(fnt);

			graphics = createGraphics(xySize, xySize);
			graphics.beginDraw();
			
			graphics.background(255);
			graphics.ellipse(xySize/2, xySize/2, xySize-circOffset, xySize-circOffset);
			
			graphics.pushMatrix();
			graphics.translate(xySize/2, xySize/2);
			
			pa.stat.update2("preparing...");
			
			// drawing circle, small indxical lines and legend-txt
			// as well as calculating the XY coordinates for each item
			for( int i = 0; i < tableSize; i++ ) {
				
				graphics.line(xySize/2-(circOffset/2),0,xySize/2-((circOffset/2)-22),0);
				graphics.fill(0);
				graphics.text( _table.getTermByIndex("row", i)  ,xySize/2-((circOffset/2)-10), -4);
				graphics.rotate(rotaFactor);
				
				coordinates[i][0] = (xySize/2 ) + (cos(i*rotaFactor) * ((xySize/2)-(circOffset/2)));
				coordinates[i][1] = (xySize/2 )+ (sin(i*rotaFactor) * ((xySize/2)-(circOffset/2)));
				
			}
			graphics.popMatrix();

			
			// drawing the actual circular data
			
			float maxRel = _table.getMaxValueTotal();
			float rel;

			for( int i = 0; i < tableSize; i++ ) {
				
				pa.stat.update2("painting... " + i+"/" +tableSize);
				
				graphics.noFill();
				PVector ctrlA, ctrlB;
				PVector mid = new PVector(xySize/2, xySize/2);

				for(int ii = 0; ii < tableSize; ii++) {
					
					rel = _table.getRelationByIndex(i, ii);
					if( rel > 0) {
						
						float tint = (50) * (rel / maxRel);
						//float tint = map(rel*(tableSize), 0, maxRel * (tableSize*tableSize), 0, 255);
						graphics.stroke(0, tint );
						
						float bow = new Float(0.5);
						
						ctrlA = new PVector(coordinates[i][0], coordinates[i][1]);
						ctrlA.sub(mid);					
						ctrlA.mult(bow);
						ctrlA.add(mid);
						
						ctrlB = new PVector(coordinates[ii][0], coordinates[ii][1]);
						ctrlB.sub(mid);
						ctrlB.mult(bow);
						ctrlB.add(mid);
						
						graphics.bezier(coordinates[i][0], coordinates[i][1], ctrlA.x, ctrlA.y, ctrlB.x, ctrlB.y, coordinates[ii][0], coordinates[ii][1]);
					}
				}
			}
			
			
			
			graphics.endDraw();
					
			
			visBool = true;
			pa.stat.completed();
			pa.stat.end();
		}
				
	 	public boolean saveVisualisation(boolean _askForName, String _suggestedName) {
			if(graphics != null) {
				//System.out.println("SICHER IST SICHER");
				
				String input;
				if (_askForName) {
					input = JOptionPane.showInputDialog("please input filename", _suggestedName);
				}
				else {
					input = _suggestedName;
				}
				
				if( input == null ) { return false; }
				
				if( input != null ) {
					graphics.save(pa.dataFolderPath+input+".jpg");
					//vis.save(input+".jpg");
					System.out.println("SAVED AS: "+pa.dataFolderPath+input+".jpg");
					
				}
				return true;
			}
			else {
				System.err.println("NO VISUALISATION TO SAVE");
				return false;
			}
			
			
		}
		
		@Override
		public boolean hasVisual() {
			return visBool;
		}

		@Override
		public VisModes getVisMode() {
			return myVismode;
		}

	}

	public class Path implements VisInterface{
		
		private static final long serialVersionUID = 8530872343427939603L;

		
		private boolean visBool = false;
		private String myVismodeString = "path";
		private VisModes myVismode = VisModes.PATH;
		private TableTypes myTablesType;
		PImage brush;
		private PGraphics graphics;
		//private PGraphics show;
		
		PFont fnt;

		public void sayHello() {
			System.out.println("Hello, this is the PathDrawer "+this.toString());
		}
		
		public void initializeParams() {
			
			VisApplet.this.frameRate(6);
			System.out.println("setup run");
			System.out.println("getting brush at " + pa.dataFolderPath+"brush1.png");
			
			

			graphics = createGraphics(100, 100);
			String fontpath = pa.dataFolderPath+"Perfect DOS VGA 437.ttf";
			System.out.println("getting font at: " + fontpath);
			
			File rootFolder = new File(".");
			System.out.println(rootFolder.getAbsolutePath());
			
			fnt = createFont(fontpath, 18, true);

			if( fnt == null ) {
				System.err.println("pFont creation failed. exiting...");
				exit();
			} else {
				System.err.println("pFont successfully created");
				graphics.textFont(fnt);
			}
			
			brush = loadImage(pa.dataFolderPath+"brush1.png");
			if(brush == null) { System.err.println("NO BRUSH"); exit(); }
			
			 VisApplet.this.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
				 public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
				 mouseWheel(evt.getWheelRotation());
				 }}); 
			noLoop();
			VisApplet.this.background(0);
		}
		
		public String getVismodeString() {
			
			return myVismodeString;
		}
				
		public TableTypes getCurrentTableType() {
			
			return myTablesType;
		}
		
	 	public void visualize(RelationTable _table) {
			
	 		visBool = false;
	 		myTablesType = _table.getTablesType();
			
			drawPath(_table);
	 		
			VisApplet.this.frame.setTitle( _table.getTablesTypeAsString()+" focal: "+_table.getFocal());
			visBool = true;
		}
		
		private void drawPath(RelationTable _table) {
			
			if( _table.hasPath() ) {
				
				pa.stat = new StatusGui();
				pa.stat.update(0, "visualizin path for "+_table.getFocal());
				
				int xySize = 1200;		// the size of the final image
				int circOffset = 200;  // how much border-space for the text
				int tableSize = _table.getRowSize();
				float rotaFactor = TWO_PI / tableSize;   // angle between items on circle in radians
				float[][] coordinates = new float[tableSize][2];
				String[] path = _table.getPath();
				
				fnt = createFont("Perfect DOS VGA 437.ttf", 2, true);
				graphics.textFont(fnt);

				graphics = createGraphics(xySize, xySize);
				graphics.beginDraw();
				
				graphics.background(255);
				graphics.ellipse(xySize/2, xySize/2, xySize-circOffset, xySize-circOffset);
				
				graphics.pushMatrix();
				graphics.translate(xySize/2, xySize/2);
				
				pa.stat.update2("preparing...");
				
				// drawing circle, small indxical lines and legend-txt
				// as well as calculating the XY coordinates for each item
				for( int i = 0; i < tableSize; i++ ) {
					
					graphics.line(xySize/2-(circOffset/2),0,xySize/2-((circOffset/2)-22),0);
					graphics.fill(0);
					graphics.text( _table.getTermByIndex("row", i)  ,xySize/2-((circOffset/2)-10), -4);
					graphics.rotate(rotaFactor);
					
					coordinates[i][0] = (xySize/2 ) + (cos(i*rotaFactor) * ((xySize/2)-(circOffset/2)));
					coordinates[i][1] = (xySize/2 )+ (sin(i*rotaFactor) * ((xySize/2)-(circOffset/2)));
					
				}
				graphics.popMatrix();
				
				for(int i = 1; i < path.length; i++) {
					
					pa.stat.update2("painting... " + i+"/" +path.length);
					graphics.noFill();
					PVector ctrlA, ctrlB;
					PVector mid = new PVector(xySize/2, xySize/2);
					
					float tint = map(path.length-i, 0, path.length, 0, 255 );
					//vis.stroke(0, 60 );
					graphics.stroke(0);
					
					float bow = new Float(0.5);
					
					int idxA = _table.getRowIndexForString(path[i-1]);
					
					ctrlA = new PVector(coordinates[idxA][0], coordinates[idxA][1]);
					ctrlA.sub(mid);					
					ctrlA.mult(bow);
					ctrlA.add(mid);
					
					int idxB = _table.getRowIndexForString(path[i]);
					
					ctrlB = new PVector(coordinates[idxB][0], coordinates[idxB][1]);
					ctrlB.sub(mid);
					ctrlB.mult(bow);
					ctrlB.add(mid);
					
					graphics.bezier(coordinates[idxA][0], coordinates[idxA][1], ctrlA.x, ctrlA.y, ctrlB.x, ctrlB.y, coordinates[idxB][0], coordinates[idxB][1]);
		
				}
				graphics.endDraw();
						
				visBool = true;
				pa.stat.completed();
				pa.stat.end();
				
			} else {
				
				JOptionPane.showMessageDialog(frame,
					    "this RelationTable has no path calculated",
					    "error",
					    JOptionPane.WARNING_MESSAGE);
				pa.closeVisFrame();
			}
			
			
		}
		
	 	public boolean saveVisualisation(boolean _askForName, String _suggestedName) {
			if(graphics != null) {
				//System.out.println("SICHER IST SICHER");
				
				String input;
				if (_askForName) {
					input = JOptionPane.showInputDialog("please input filename", _suggestedName);
				}
				else {
					input = _suggestedName;
				}
				
				if( input == null ) { return false; }
				
				if( input != null ) {
					graphics.save(pa.dataFolderPath+input+".jpg");
					//vis.save(input+".jpg");
					System.out.println("SAVED AS: "+pa.dataFolderPath+input+".jpg");
					
				}
				return true;
			}
			else {
				System.err.println("NO VISUALISATION TO SAVE");
				return false;
			}
			
			
		}
		
		@Override
		public boolean hasVisual() {
			return visBool;
		}

		@Override
		public VisModes getVisMode() {
			return myVismode;
		}

	}


}