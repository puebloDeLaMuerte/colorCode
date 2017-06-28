package colorcodetst;


import java.awt.Color;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.util.ArrayList;
import java.util.LinkedHashMap;


import MyUtils.StatusGui;
import MyUtils.VisModes;
import MyUtils.TableTypes;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.event.MouseEvent;

public class VisApplet extends PApplet implements VisInterface{
	
	private static final long serialVersionUID = 4722827600944467489L;
	public VisInterface vis;
	private JFrame frame;
	private ColorCodeTST pa;
	private int mouseXOffset, mouseYOffset;
	private PGraphics display;
	private int frameCount = 0;
	private int mousePX, mousePY;
	//private long timeSinceLastDraw;
	//private long timeOfLastDraw;
	
	private RelationTable tableToVisualize = null;
	private boolean doNewVisualisation = false;
	private boolean pauseToggle;
	
	public void setup() {
		
		size(frame.getWidth(), frame.getHeight());
		//frameRate(1);
		
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
			
			if(pauseToggle) {
				pushStyle();
				noStroke();
				fill(240,10,20);
				rect(30,30,6,17);
				rect(40,30,6,17);
				popStyle();
			}
			
		}
		else System.out.println("vis seems to be NULL...");
		
		
		if(doNewVisualisation && tableToVisualize != null) {
			vis.visualize(tableToVisualize);
			tableToVisualize = null;
		}
		if( doNewVisualisation && tableToVisualize == null && vis != null && vis.hasVisual() ) {
			
			display = vis.getVisualisationGraphics();
			doNewVisualisation = false;
		}
		
		if( frameCount != 0 && vis != null && vis.getVisMode() == VisModes.NEBULAR ) {
			
			if(!pauseToggle) vis.updateFrame();
			vis.drawFrame();
			display = vis.getVisualisationGraphics();
			//System.out.println("frame updated");
		}
		
		frameCount++;
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

	public void updateFrame(){
		
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

	public void setVisualisationParams(VisModes _mode) {
		
		if( vis == null || vis.getVisMode() != _mode ) changeVisMode(_mode);
	}
	
	private void changeVisMode( VisModes _mode ) {
		
		switch (_mode) {
		case GRID_HARD:
			vis = new GridHard();
			break;
		case GRID_SOFT:
			vis = new GridSoft();
			break;
		case CIRCULAR:
			vis = new Circular();
			break;
		case PATH:
			vis = new Path();
			break;
		case NEBULAR:
			vis = new Nebular();
			break;

		default:
			break;
		}
	}
	
	@Override
	public void visualize(RelationTable _table) {
		tableToVisualize = _table;
		doNewVisualisation = true;
	}

	@Override
	public boolean saveVisualisation(boolean _askForName, String _suggestedName) {
		if( vis != null && vis.hasVisual() ) {
			return vis.saveVisualisation(_askForName, _suggestedName);
		}
		else return false;
	}	

	public PGraphics getVisualisationGraphics() {
		return null;
	}
	
	private void setVisualisationGraphics( PGraphics _g) {
		display = _g;
	}
	
	@Override
	public void setDisplayZoomValue(float _zoom) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDisplayOffset(int _xOffset, int _Yoffset) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawFrame() {
		// TODO Auto-generated method stub

	}

	public void mouseWheel(MouseEvent event) {
		float e = event.getCount();
		if(vis != null) vis.setDisplayZoomValue(e);
	}
	
	public void mousePressed(MouseEvent e){
		mousePX = e.getX();
		mousePY = e.getY();
	}
	
	public void mouseDragged(MouseEvent e){
		if(vis != null) vis.setDisplayOffset(e.getX()-mousePX, e.getY()-mousePY);
		mousePX = e.getX();
		mousePY = e.getY();
	}

	public void keyPressed() {
		
		if( key == ' ') {
			pauseToggle = !pauseToggle;
		}
	}
	
	
		
	//||\\||//||\\||//||\\||//||\\||//||\\||//||\\||//||\\||//||\\||//||\\||//||\\||

	
	public class Nebular implements VisInterface {

		private static final long serialVersionUID = 2990147396825116184L;

		private String myVismodeString = "nebular";
		private VisModes myVismode = VisModes.NEBULAR;
		private TableTypes myTablesType;
		private boolean visBool = false;
		private RelationTable myTable;
		private PGraphics graphics;

		private float zoomfactor = 1;
		private int xOffset = 0, yOffset = 0;
		
		private Element[] elements;
		private float maxPosition;
		
		private float maximumInitialSpread = 80;
		
//		private float maximumDrawSpread = 200;
		private float maximumDrawSpread = 17;
//		private float maximumDrawSpread = 1199800;
		
		//private float influenceFactor = (float)0.00000005;
		private float influenceFactor = (float)0.0005;
		//private float influenceFactor = (float)15;
		
		private float repulsionRate = -0.6f;
//		private float repulsionRate = -4.6f;
		
		private int visualisationSize = 1300;
//		private int visualisationSize = 600;
		
		private int saveFrameCount = 0;
		private int updateCount = 0;
		
		private long updateTime = 0;
		private long drawTime	= 0;
		
		
		public Nebular(){
			
			System.out.println("Neb established");
		}
		
		private void prepareIt(RelationTable _table){
			
			this.graphics = createGraphics(10, 10);
			this.graphics.setSize(visualisationSize	, visualisationSize);
			this.graphics.beginDraw();
			this.graphics.background(255);
			this.graphics.endDraw();
			
			myTable = _table;
			
			// create the Elements
			
			LinkedHashMap<Integer, String> idx_col;
			LinkedHashMap<Integer, String> idx_row;
			int elemCount;
			

			//  Put multiple Elements for visualisation
			if( true ) {
				
				ArrayList<Element> elementsList = new ArrayList<VisApplet.Nebular.Element>();
				//elements = new Element[myTable.getRowSize()*myTable.getRowSize()];

				elemCount = 0;
				idx_col = myTable.getColumnIndex();
				idx_row = myTable.getRowIndex();

				for( int r=0; r<idx_row.size(); r++) {

					String currentRowTerm = idx_row.get(r);

					Color theColor = new Color((int)random(40,200), (int)random(40,200), (int)random(40,200));


					for( int c=0; c<idx_col.size(); c++) {

						int rel = myTable.getRelationByIndex(r, c);
						
						if(rel != 0 ) {
							elementsList.add( new Element(		
									currentRowTerm, 
									idx_col.get(c), 
									rel, 
									theColor,
									elemCount
									));

							elemCount++;
						}
					}

				}
				elements = elementsList.toArray(new Element[elemCount]);
			}

			//   Put only single Elemnts for visualisation
			else {
				
				elements = new Element[myTable.getRowSize()];
				elemCount = 0;
				idx_col = myTable.getColumnIndex();
				idx_row = myTable.getRowIndex();

				for( int r=0; r<idx_row.size(); r++) {

					String currentRowTerm = idx_row.get(r);

					Color theColor = new Color((int)random(40,200), (int)random(40,200), (int)random(40,200));


					elements[r] = new Element(
							currentRowTerm, 
							"", 
							0,
							theColor,
							r
							);
				}

			}

		}
		
		public void updateFrame() {
			
			long updateStart = millis();
			
			for( Element e : elements) {
				e.findDirection();
			}
//			maxPosition = 0;
			maxPosition = 500;
			for( Element e : elements) {
				e.move();
			}
			
//			saveVisualisation(false, myTablesType+"_"+"PointCloud_frame"+nfs(saveFrameCount++, 5)+"_nova");
			
			updateCount++;
			updateTime = millis() - updateStart;
			System.out.println("update time: " + updateTime);
		}
		
		public void drawFrame() {
//			long drawStart = millis();
			drawIt();
//			drawTime = millis() - drawStart;
//			System.out.println("draw time: " + drawTime);
		}
		
		private void drawIt() {
			
			//maximumDrawSpread = maxPosition;
			
			//this.graphics = createGraphics(10, 10);
			//this.graphics.setSize(visualisationSize	, visualisationSize);
			this.graphics.beginDraw();
			
			this.graphics.background(255);
			
			this.graphics.pushMatrix();
			this.graphics.translate(this.graphics.width/2, this.graphics.height/2);
			this.graphics.stroke(0);
			if( false ) {
				
				// draw crosshair
				
				this.graphics.line(10, 0, 20, 0);
				this.graphics.line(-10, 0, -20, 0);
				this.graphics.line(0, 10, 0, 20);
				this.graphics.line(0, -10, 0, -20);
			}
			
			this.graphics.noStroke();
			
			for(Element e: elements) {
				
				//this.graphics.stroke(e.myColor.getRGB());
				this.graphics.fill(e.myColor.getRGB(), 160);
				//this.graphics.point(mappedPos(e.getXpos()), mappedPos(e.getYpos()));
				//this.graphics.ellipse(mappedXPos(e.getXpos())-1, mappedYPos(e.getYpos())-1, 3, 3);
				//this.graphics.fill(e.myColor.getRGB(), 70);
				this.graphics.ellipse(mappedXPos(e.getXpos())-2, mappedYPos(e.getYpos())-2, 5, 5);

				//this.graphics.line(0,0,mappedXPos(e.getXpos()), mappedYPos(e.getYpos()));
				
			}
			this.graphics.popMatrix();
			this.graphics.fill(0);
			this.graphics.text("frameCount: "+frameCount, 20, 20);
			this.graphics.endDraw();
			
		}
		
		int mappedXPos(float _in ){
						
			return (int)map(_in, 0, maxPosition, 0+xOffset, zoomfactor*((visualisationSize/2)-150)+xOffset );

		}
		
		int mappedYPos(float _in ){
			
			return (int)map(_in, 0, maxPosition, 0+yOffset, zoomfactor*((visualisationSize/2)-150)+yOffset );

		}
		
		private class Element {
			
			private String 		myTerm, myLove;
			private int			myTermHash;
			private int 		myLoveLevel;
			private PVector 	myPos, myDirection;
			private Color 		myColor;
			private int 		myID;
			
			
			public Element( String _myTerm, String _myLove, int _loveLevel, Color _color, int _id) {
				
				myID = _id;
				
				myTerm = _myTerm;
				myTermHash = myTerm.hashCode();
				
				myLove = _myLove;
				myLoveLevel = _loveLevel;
				
				myDirection = new PVector(0,0);
				
				//myPos = setNewCircularRandomPosition();
				myPos = setNewSquaredRandomPosition();
				
				myColor = _color;
				
				//System.out.println("pos: "+ myPos.x +" / "+myPos.y + "  "+myTerm);
			}
			
			private PVector setNewCircularRandomPosition() {
				PVector newRandomPosition = PVector.fromAngle((float)random(TWO_PI));
				newRandomPosition.normalize();
				newRandomPosition.setMag(random(maximumInitialSpread));
				return newRandomPosition; 
			}
			
			private PVector setNewSquaredRandomPosition() {
				
				return new PVector( random(-maximumInitialSpread, maximumInitialSpread), random(-maximumInitialSpread, maximumInitialSpread) );
				
			}
			
			public void findDirection() {
				
				//myDirection.mult((float)0.5);
				
//				boolean effect = false;
				
				// motion damper:
				myDirection.mult(0.7f);
				
				for( Element e : elements) {
					
					
					// TODO take into account the special love i have
					// TODO take into account the others special love
					
					if ( /*myID != e.getID() && */ myTermHash != e.getTermHash() ) {
					
												
						PVector thisInfluenceDirection = new PVector(e.getPos().x, e.getPos().y);
						thisInfluenceDirection.sub(myPos);
						
						float thisDistance = thisInfluenceDirection.magSq();
						//thisDistance = thisDistance*thisDistance;
						
						if( thisDistance < (maximumDrawSpread) ) {
							
//							effect = true;

							thisInfluenceDirection.normalize();

							float thisInfluenceMag;
							int thisRelation =  myTable.getRelation( myTerm, e.getTerm());

							if (thisRelation == 0) {
								thisInfluenceMag = (float) repulsionRate / thisDistance;
//								thisInfluenceMag = 0f;
							} else {

								float thisRelationSquared = (float) (thisRelation * thisRelation);
								//thisInfluenceMag = thisRelation;
								thisInfluenceMag = (float) influenceFactor * thisRelationSquared / thisDistance;

							}
							thisInfluenceDirection.mult(thisInfluenceMag);
							//thisInfluenceDirection.mult(influenceFactor);
							myDirection.add(thisInfluenceDirection);
						}
					}
					
				}
				
//				if( effect ) {
//					myColor = new Color(255,0,255);
//				} else {
//					myColor = new Color(0);
//				}
			}
			
			public void move() {
				
				myPos.add(myDirection);
				
//				if( abs(myPos.x) > abs(maxPosition) ) maxPosition = abs(myPos.x);
//				if( abs(myPos.y) > abs(maxPosition) ) maxPosition = abs(myPos.y);
			}
			
			public String getTerm(){
				return myTerm;
			}
			
			public int getTermHash() {
				return myTermHash;
			}
			
			public PVector getPos() {
				return myPos;
			}
			
 			public float getXpos() {
				return myPos.x;
			}
			
			public float getYpos() {
				return myPos.y;
			}
			
			public int getID() {
				return myID;
			}
		}
		
		
		////////////
		
		@Override
		public void sayHello() {

			System.out.println("this is the Nebular visualisation modul sayin hi");
		}

		@Override
		public boolean hasVisual() {
			return visBool;
		}

		@Override
		public String getVismodeString() {
			return myVismodeString;
		}

		@Override
		public TableTypes getCurrentTableType() {

			return myTablesType;
		}

		@Override
		public void visualize(RelationTable _table) {
			visBool = false;
	 		myTablesType = _table.getTablesType();
			

	 		prepareIt(_table);
			
			drawIt();

	 		
			frame.setTitle( _table.getTablesTypeAsString()+" focal: "+_table.getFocal());
			visBool = true;
			System.out.println("DONE DONE DONE RIGHT");			
		}

		@Override
		public boolean saveVisualisation(boolean _askForName,
				String _suggestedName) {
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
		public VisModes getVisMode() {
			return myVismode;
		}

		public PGraphics getVisualisationGraphics() {
			if( visBool) return graphics;
			else return null;
		}

		@Override
		public void setDisplayZoomValue(float _zoom) {
			
			zoomfactor += _zoom/4;
			if(zoomfactor <= 0) zoomfactor = (float)0.1;
		}

		@Override
		public void setDisplayOffset(int _xOffset, int _Yoffset) {
			xOffset += _xOffset;
			yOffset += _Yoffset;
			
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
		private PImage brush;
		private PGraphics graphics;
		//private PGraphics show;
		
		PFont fnt;
		
		public GridSoft(){
			initialize();
		}
		
		public void sayHello() {
			System.out.println("Hello, this is the softGridDrawer module "+this.toString());
		}
		
		public void initialize() {
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
			visBool = true;
			System.out.println("DONE DONE DONE RIGHT");
		}
				
		private void drawGridSoft(RelationTable _table) {
			
			pa.stat = new StatusGui();
			pa.stat.update(0, "generating one hell of a visualisation");
			
			int cellSize = 12;
			int noOfCells = _table.getRowSize();
			int xySize = 200 + ( cellSize * noOfCells );

			this.graphics = createGraphics(xySize, xySize);
			brush = loadImage(pa.dataFolderPath+"brush1.png");
			
			this.graphics.beginDraw();
			this.graphics.background(bg);
			this.graphics.noStroke();

			//vis.image(brush,200,200);
			
			for(int x = 0; x<noOfCells; x++ ) {
				for(int y = 0; y<noOfCells; y++) {
					
					//vis.fill(  230-(_table.getRelationByIndex(x, y)*10)  );
					//vis.rect(100+(x*(cellSize)), 100+(y*(cellSize)), cellSize, cellSize);
					this.graphics.tint(255, _table.getRelationByIndex(x, y)*2 );
					this.graphics.image(brush,40+(x*(cellSize)), 40+(y*(cellSize)));
					
				}
			}
			
			
			this.graphics.endDraw();
			//show = vis;
			pa.stat.end();
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
			return visBool;
		}

		public VisModes getVisMode() {
			return myVismode;
		}
		
		public PGraphics getVisualisationGraphics() {
			if( visBool) return graphics;
			else return null;
		}

		@Override
		public void updateFrame() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setDisplayZoomValue(float _zoom) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setDisplayOffset(int _xOffset, int _Yoffset) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void drawFrame() {
			// TODO Auto-generated method stub
			
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
		private PGraphics graphics;
		//private PGraphics show;
		
		PFont fnt;

		public GridHard() {
			this.sayHello();
			initializeParams();
		}
		
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
			
			 VisApplet.this.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
				 public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
				 mouseWheel(evt.getWheelRotation());
				 }}); 
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
			visBool = true;
			System.out.println("DONE DONE DONE DONE DONE DONE");
		}
		
		private void drawGridHard(RelationTable _table) {
			
			pa.stat = new StatusGui();
			pa.stat.update(0, "generating one hell of a visualisation");
			
			//int maxTint = pa.keywordsList.length;


			int noOfCells = _table.getRowSize();
			int maxTint = _table.getMaxValueTotal();

			int xySize = margin + ( cellSize * noOfCells );

			this.graphics = createGraphics(100, 100);
			fnt = createFont("Perfect DOS VGA 437.ttf", cellSize-2, true);

			this.graphics.setSize(xySize, xySize);
			
			//this.graphics.setSize(190, 190);
			this.graphics.textFont(fnt);


			
			this.graphics.beginDraw();
			this.graphics.background(bg);
			this.graphics.noStroke();

			this.graphics.pushMatrix();
			this.graphics.fill(0);
			this.graphics.rotate(HALF_PI/2);
			this.graphics.text( _table.getFocal(), 20,0);
			this.graphics.popMatrix();
			
			String term;

			for(int x = 0; x<noOfCells; x++ ) {
				
				term = _table.getTermByIndex("column", x);
				if(term.length()>8) term = term.substring(0, 8);
				
				this.graphics.pushMatrix();
				this.graphics.translate((margin/2)+2+(x*cellSize), 10);
				this.graphics.rotate(HALF_PI);
				this.graphics.fill(0);
				this.graphics.text(term, 0,0);
				this.graphics.popMatrix();
				
				
				for(int y = 0; y<noOfCells; y++) {
					
					
					if(x==0) {
						term = _table.getTermByIndex("row", y);
						this.graphics.fill(0);
						this.graphics.text(term, 10, (margin/2)+(cellSize-2)+(y*cellSize));
					}
					
					this.graphics.fill(  map(_table.getRelationByIndex(y, x), 0, maxTint, 255, 0)  );
					this.graphics.rect((margin/2)+(x*(cellSize)), (margin/2)+(y*(cellSize)), cellSize, cellSize);
				}
			}
			
			
			this.graphics.endDraw();
			//show = vis;
			pa.stat.end();
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
	 		return visBool;
	 	}

	 	@Override
	 	public VisModes getVisMode() {
	 		return myVismode;
	 	}

		public PGraphics getVisualisationGraphics() {
			if( visBool) return graphics;
			else return null;
		}

		@Override
		public void updateFrame() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setDisplayZoomValue(float _zoom) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setDisplayOffset(int _xOffset, int _Yoffset) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void drawFrame() {
			// TODO Auto-generated method stub
			
		}

	}
	
	public class Circular implements VisInterface{
		
		private static final long serialVersionUID = 8530872343427939603L;
				
		private boolean visBool = false;
		private String myVismodeString = "circular";
		private VisModes myVismode = VisModes.CIRCULAR;
		private TableTypes myTablesType;
		private PImage brush;
		private PGraphics graphics;
		//private PGraphics show;
		
		PFont fnt;

		public Circular() {
			initializeParams();
		}
		
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

		public PGraphics getVisualisationGraphics() {
			if( visBool) return graphics;
			else return null;
		}

		@Override
		public void updateFrame() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setDisplayZoomValue(float _zoom) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setDisplayOffset(int _xOffset, int _Yoffset) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void drawFrame() {
			// TODO Auto-generated method stub
			
		}

	}

	public class Path implements VisInterface{
		
		private static final long serialVersionUID = 8530872343427939603L;

		
		private boolean visBool = false;
		private String myVismodeString = "path";
		private VisModes myVismode = VisModes.PATH;
		private TableTypes myTablesType;
		private PImage brush;
		private PGraphics graphics;
		//private PGraphics show;
		
		PFont fnt;

		public Path() {
			initializeParams();
		}
		
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

		public PGraphics getVisualisationGraphics() {
			if( visBool) return graphics;
			else return null;
		}

		@Override
		public void updateFrame() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setDisplayZoomValue(float _zoom) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setDisplayOffset(int _xOffset, int _Yoffset) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void drawFrame() {
			// TODO Auto-generated method stub
			
		}


	}

	



}