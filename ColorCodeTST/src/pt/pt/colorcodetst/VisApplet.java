package colorcodetst;


import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.event.MouseEvent;
import pt.pt.colorcode.utils.StatusGui;
import pt.pt.colorcode.utils.TableTypes;
import pt.pt.colorcode.utils.VisModes;
import pt.pt.colorcode.utils.metadata.DataField;
import pt.pt.colorcode.utils.metadata.MetaDater;
import pt.pt.colorcode.utils.quicksort.Quicksort;
import pt.pt.colorcode.utils.quicksort.SortElement;

public class VisApplet extends PApplet implements VisInterface{
	
	private static final long serialVersionUID = 4722827600944467489L;
	public VisInterface visInterface;
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
		
		if( visInterface == null || !visInterface.hasVisual() ) {
			
			background(0);
			fill(255);
			text("visualizing...", random(width), random(height));
			
		}
		else if ( display != null ) {	
			image(display,0,0,width, height);
//			background(display);
//			set(0, 0, display);
//			display = null;
		}
		else System.out.println("visApplet seems to be NULL...");
		
		
		if(doNewVisualisation && tableToVisualize != null) {
			visInterface.visualize(tableToVisualize);
			tableToVisualize = null;
		}
		
		if( visInterface != null && visInterface.getClass().equals(Nebular.class) ) {

			if( visInterface.isUpdateable() ) {
			
				if(!pauseToggle) visInterface.updateFrame();
				
				visInterface.drawFrame();
				display = visInterface.getVisualisationGraphics();
				image(display,0,0,width, height);
			}
		}
		
		if( doNewVisualisation && tableToVisualize == null && visInterface != null && visInterface.hasVisual() ) {
			
			display = visInterface.getVisualisationGraphics();
			doNewVisualisation = false;
		}
		
		if(pauseToggle) {
			pushStyle();
			noStroke();
			fill(240,10,20);
			rect(30,30,6,17);
			rect(40,30,6,17);
			popStyle();
		}
		frameCount++;
	}
	
	public void mouseWheel(int delta) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean hasVisual() {
		if( visInterface != null && visInterface.hasVisual() ) return true;
		else return false;
	}
	
	public void setJFrame(JFrame _frame) {
		this.frame = _frame;
	}

	public void setParent(ColorCodeTST _parent) {
			pa = _parent;
	}

	@Override
	public void sayHello() {
		if( visInterface != null ) {
			visInterface.sayHello();
		}
		else System.out.println("this is the VisApplet. There doesn't seem to be a visInterface in place, sorry!");
		
	}

	public void updateFrame(){
		
	}
	
	@Override
	public String getVismodeString() {
		if( visInterface != null ) {
			return visInterface.getVismodeString();
		}
		else return "visApplet is null";
	}

	public VisModes getVisMode() {
		if( visInterface != null ) return visInterface.getVisMode();
		else return null;
	}
	
	@Override
	public pt.pt.colorcode.utils.TableTypes getCurrentTableType() {
		if( visInterface != null ) {
			return visInterface.getCurrentTableType();
		}
		else return null;
	}

	public void setVisualisationParams(VisModes _mode, TableTypes type, boolean savebool) {
		

			changeVisMode(_mode);

			visInterface.setType(type);
			
			if( visInterface.getClass().equals(Nebular.class)) {
				
				Nebular neb = (Nebular)visInterface;
				
				neb.setSave(savebool);
			}
		
	}
	
	
	private void changeVisMode( VisModes _mode ) {
		
		System.out.println("changing visMode");
		
		if( visInterface != null ) {
			visInterface.destroy();
			visInterface = null;
			System.gc();
		}
		
		switch (_mode) {
		case GRID_HARD:
			visInterface = new GridHard();
			break;
		case GRID_SOFT:
			visInterface = new GridSoft();
			break;
		case CIRCULAR:
			visInterface = new Circular();
			break;
		case PATH:
			visInterface = new Path();
			break;
		case NEBULAR:
			visInterface = new Nebular();
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
		if( visInterface != null && visInterface.hasVisual() ) {
			return visInterface.saveVisualisation(_askForName, _suggestedName);
		}
		else return false;
	}	

	public PGraphics getVisualisationGraphics() {
		return null;
	}
	
//	private void setVisualisationGraphics( PGraphics _g) {
//		display = _g;
//	}
	
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
		if(visInterface != null) visInterface.setDisplayZoomValue(e);
	}
	
	public void mouseClicked() {
		pauseToggle = !pauseToggle;
	}
	
	public void mousePressed(MouseEvent e){
		mousePX = e.getX();
		mousePY = e.getY();
	}
	
	public void mouseDragged(MouseEvent e){
		if(visInterface != null) visInterface.setDisplayOffset(e.getX()-mousePX, e.getY()-mousePY);
		mousePX = e.getX();
		mousePY = e.getY();
	}

	@Override
	public void keyPressed() {
		
		if( key == ' ') {
			pauseToggle = !pauseToggle;
		}
		
		System.err.println("key 1");
	}
	
	@Override
	public void keyPressed(processing.event.KeyEvent e) {
		
		key = e.getKey();
		
		if( key == ' ') {
			pauseToggle = !pauseToggle;
		}
		System.err.println("key 2");
	}
	
	@Override
	public boolean setType(TableTypes _type) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void destroy() {
		this.visInterface.destroy();
		this.visInterface = null;
		this.pa = null;
		this.display = null;
		super.destroy();
	}
	
	//||\\||//||\\||//||\\||//||\\||//||\\||//||\\||//||\\||//||\\||//||\\||//||\\||

	
	public class Nebular implements VisInterface {

		private static final long serialVersionUID = 2990147396825116184L;

		private String 			myVismodeString = "nebular";
		private VisModes 		myVismode = VisModes.NEBULAR;
		private boolean			save = false;
		private String			folderName;
		private String			fileName;
		
		private TableTypes 		myTablesType;
		private boolean 		visBool = false;
		private boolean			prepared = false;
		private RelationTable 	myTable;
		private PGraphics 		graphics;

		private float zoomfactor = 1;
		private int xOffset = 0, yOffset = 0;
		
		private long	meanUpdateTime = -1;;
		
		private Element[] elements, sortedElementsX, sortedElementsY;
		int prevboundMin;
		int prevboundMax;
		private float maxPosition;
		
//		private float maximumInitialSpread = 200;//80;
		private float maximumInitialSpread = 160;
		
		private int   maximumDistanceAffected = 14;
		private float maximumDrawSpread = 200;
//		private float maximumDrawSpread = 18;
//		private float maximumDrawSpread = 1199800;
		
		private float influenceFactor = (float)0.000005;
//		private float influenceFactor = (float)0.0005;
//		private float influenceFactor = (float)15;
		
		
		private float repulsionRate = -0.0f;
//		private float repulsionRate = -0.6f;
//		private float repulsionRate = -1.2f;
//		private float repulsionRate = -4.6f;
		
		private int visualisationSize = 1300;
//		private int visualisationSize = 600;
		
		private int saveFrameCount = 0;
		private int updateCount = 0;
		
		private long updateTime = 0;
		private long drawTime	= 0;

		public int influences;
		
		
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
			
			
			boolean hist = false;
			

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
									elemCount,
									hist
									));
//							first = false;
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
							r,
							false
							);
				}

			}
			
			sortedElementsX = new Element[elements.length];
			sortedElementsY = new Element[elements.length];
			
			int prevboundMin = 0;
			int prevboundMax = elements.length;
			
			
			if( save ) {
				
				ArrayList<DataField> list = new ArrayList<DataField>();
				
				list.add(new DataField("vismode", myVismodeString) );
				list.add( new DataField("filename", fileName));
				DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH:mm");
				Date date = new Date();
				list.add( new DataField("datecreated", date.toString()) );
				list.add( new DataField("canvassize", visualisationSize) );
				
				
				list.add(new DataField("tabletype", myTablesType.toString() ) );
				list.add( new DataField("tablesize", myTable.getRowSize()) );
				list.add( new DataField("elementscount", elements.length) );
				list.add( new DataField("initialspread", maximumInitialSpread) );
				list.add( new DataField("maxdrawspread", maximumDrawSpread) );
				list.add( new DataField("influencefactor", influenceFactor) );
				list.add( new DataField("repulsionrate", repulsionRate) );
			
				String filename = folderName + "/" + fileName + "_metadata.txt"; 
				try {
					MetaDater.saveMetadataToFile(new File(filename), list);
					System.out.println("metadata saved to file: filename");
				} catch (IOException e) {
					System.err.println("error during metadata saving");
					e.printStackTrace();
				}
			}
			
			prepared = true;
		}
		
		public boolean isUpdateable() {
			
			return prepared;
		}
		
		public void updateFrame() {
			
			
			
			long updateStart = millis();
			influences = 0;
			
			// Timer
//			double Sorting_start = System.nanoTime();
			
			SortElement[] elemsX = new SortElement[elements.length];
//			SortElement[] elemsY = new SortElement[elements.length];
			
			for (int i = 0; i < elemsX.length; i++) {

				elemsX[i] = new SortElement(elements[i], elements[i].getXpos());
//				elemsY[i] = new SortElement(elements[i], elements[i].getYpos());
			}
			
			Quicksort.sort(elemsX);
//			Quicksort.sort(elemsY);
			
			int x = 0;
			
			for (SortElement sortElement : elemsX) {
				sortedElementsX[x] = (Element) sortElement.getObject();
				sortedElementsX[x].setIndexX(x);
				//				System.out.println(x+": "+sortedElementsX[x].getXpos());
				x++;
			}
//			x = 0;
//			for (SortElement sortElement : elemsY) {
//				sortedElementsY[x] = (Element) sortElement.getObject();
//				sortedElementsY[x].setIndexY(x);
////								System.out.println(x+": "+sortedElementsY[x].getYpos());
//				x++;
//			}
//			System.out.println("measured time for Sorting X: " + ((System.nanoTime() - Sorting_start) / 1000000d) + " milliseconds");
//			System.out.println("X: sortElement[]: "+elemsX.length +" Element[]: " + sortedElementsX.length);
//			System.out.println("Y: sortElement[]: "+elemsY.length +" Element[]: " + sortedElementsY.length);

			
			
//			System.out.println("\n\n\n\n");
			
			prevboundMin = 0;
			prevboundMax = 0;
			
			
			for( Element e : sortedElementsX) {
				e.findDirectionSorted();
//				System.out.println(e.getIndexX() + ": " + prevboundMin);
			}
			
//			maxPosition = 0;
			maxPosition = 500;
			for( Element e : elements) {
				e.move();
			}
			
			
			
			updateCount++;
			updateTime = millis() - updateStart;
			
			if( updateCount < 2 ) {
				meanUpdateTime = updateTime; 
			} else  {
				meanUpdateTime = (long)(meanUpdateTime / 10 * 9) + (long)(updateTime / 10);
			}
			
			if( updateCount % 10 == 0 ) {
				
				System.out.println("updates: " + updateCount + ", mean update time: " + meanUpdateTime);
			}
//			System.out.println("influences: " + influences);
//			System.out.println("update time: " + updateTime);
		}
		
		public void drawFrame() {
//			long drawStart = millis();
			drawIt();
//			drawTime = millis() - drawStart;
//			System.out.println("draw time: " + drawTime);
			
			if (save && !pauseToggle) {
				saveVisualisation(false, folderName+"/"+ fileName+ "_" + nfs(saveFrameCount++, 5));
			}
			
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
				
				
				this.graphics.fill(e.myColor.getRGB(), 160);
				
				this.graphics.ellipse(mappedXPos(e.getXpos())-2, mappedYPos(e.getYpos())-2, 5, 5);
				
//				if( e.hasHistory && e.history.size() > 3) {
//					
//
//					
////					this.graphics.pushStyle();
////					this.graphics.fill(0);
////					this.graphics.stroke(0);
//					
//					PVector[] vecs = (PVector[]) e.history.toArray(new PVector[e.history.size()]);
//					
//					for (int i = 0; i < vecs.length-1; i++) {
//						PVector v1 = vecs[i];
//						PVector v2 = vecs[i+1];
//						
//						PVector len = PVector.sub(v1, v2);
//						float length = (float)(abs(len.mag()));
//						if(length > 2f) length = 2f;
//						float intense = (float)(255f * (-1f * (length - 2f)));
//						this.graphics.stroke(e.myColor.getRGB(), intense );
//						this.graphics.strokeWeight(intense / 63);
//						
//						this.graphics.line(mappedXPos(v1.x), mappedYPos(v1.y), mappedXPos(v2.x), mappedYPos(v2.y));
//					}
////					this.graphics.popStyle();
//				}
				
			}
			this.graphics.popMatrix();
			this.graphics.fill(0);
			this.graphics.text("updates: "+ updateCount, 20, 20);
			this.graphics.text("zoom   : "+ zoomfactor, 20, 40);
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
			private int			myIndexX, myIndexY;
			private Color 		myColor;
			private int 		myID;
			
			public boolean		hasHistory;
			public ArrayList<PVector> history;
			private boolean satisfied;
			
			public Element( String _myTerm, String _myLove, int _loveLevel, Color _color, int _id, boolean hasHistory) {
				
				
				
				myID = _id;
				
				myTerm = _myTerm;
				myTermHash = myTerm.hashCode();
				
				myLove = _myLove;
				myLoveLevel = _loveLevel;
				
				myDirection = new PVector(0,0);
				
				
				this.hasHistory = hasHistory;
				
				if( hasHistory ) {
					this.history = new ArrayList<PVector>();

//					myPos = new PVector(0, 0);
				}
				
//				
				myPos = setNewSquaredRandomPosition();
//				myPos = setNewCircularRandomPosition();
				
				
				
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
			
			public void findDirectionSorted() {
								
				
				// motion damper:
				myDirection.mult(0.7f);
				
				
				// Timer
//				double SortBoundaryFinder_start = System.nanoTime();
				
//				prevboundMin = myIndexX;
//				prevboundMax = myIndexX;
				
//				int[] Xbounds = findSortBoundaryX();
				findSortBoundaryX();
				
				
//				int[] Ybounds = findSortBoundaryY();
//				System.out.println("measured time for SortBoundaryFinder : " + ((System.nanoTime() - SortBoundaryFinder_start) / 1000000d) + " milliseconds");
//				System.out.println(Xbounds[0] +" / " + Xbounds[1]);

				
				
				
				// approach: sorted array compare:
				
//				int[] Ybounds = findSortBoundaryY();
				
//				hashCompare(Xbounds, Xbounds);
				
//				System.out.println("\n\n\n\n");
				
				
				// approach: first sort the remaining values by Y and then cut out the middle of that
				
				
//				SortElement[] elemsY = new SortElement[Xbounds[1] + (Xbounds[0]*-1) ];
//				int j = 0;
//				for(int i = myIndexX + Xbounds[0];  i < myIndexX + Xbounds[1];  i++ ) {
//					elemsY[j] = new SortElement(sortedElementsX[i], sortedElementsX[i].getYpos());
//					j++;
//				}
//				
//				Quicksort.sort(elemsY);
//				
//				sortedElementsY = new Element[elemsY.length];
//				
//				int x = 0;
//				for (SortElement sortElement : elemsY) {
//					sortedElementsY[x] = (Element)sortElement.getObject();
//					sortedElementsY[x].setIndexY(x);
////					System.out.println(x+": "+sortedElementsX[x].getXpos());
//					x++;
//				}
//				
//				int[] Ybounds = findSortBoundaryY();
//				
//				
//
//				for(int i = myIndexY + Ybounds[0];  i < myIndexY + Ybounds[1];  i++ ) {
//				for(int i = myIndexX + Xbounds[0];  i < myIndexX + Xbounds[1];  i++ ) {
//				for(int i = Xbounds[0];  i <  Xbounds[1];  i++ ) {
				for(int i = prevboundMin;  i <  prevboundMax;  i++ ) {

					
					Element e = sortedElementsX[i];
					
					// TODO take into account the special love i have
					// TODO take into account the others special love
					
					if ( /*myID != e.getID() && */ myTermHash != e.getTermHash()){// && !e.isSatisfied()) {
					
												
						PVector thisInfluenceDirection = new PVector(e.getPos().x, e.getPos().y);
						thisInfluenceDirection.sub(myPos);
						
						float thisDistance = thisInfluenceDirection.magSq();
//						float thisrealDistance = thisInfluenceDirection.mag();
						
						if( thisDistance < (maximumDrawSpread) ) {
							

							thisInfluenceDirection.normalize();

							int thisRelation =  myTable.getRelation( myTerm, e.getTerm());
							
							float thisInfluenceMag = 1 / (thisRelation + 0.000001f);
							
							thisInfluenceMag = thisInfluenceMag / thisDistance * influenceFactor * -1;


							thisInfluenceDirection.mult(thisInfluenceMag);

							myDirection.add(thisInfluenceDirection);
//							influences++;
//							thisInfluenceDirection.mult(-1f);
//							e.addDirection(thisInfluenceDirection);
//							influences++;
						}
					}
					
				}
//				this.satisfied = true;
			}
			
			public void addDirection(PVector v) {
				myDirection.add(v);
			}
			
			public boolean isSatisfied() {
				return satisfied;
			}
			
			private void hashCompare(int[] x, int[] y) {
				
				ArrayList<Element> ret = new ArrayList<Element>();
				
				
				
				//convert arr1 to java.util.Set
				Set<Element> set1 = new HashSet<Element>();
				
				for (int i = myIndexX + x[0];  i < myIndexX + x[1];  i++ ) {
					set1.add(sortedElementsX[i]);
				}

				// print the duplicates
				for (int i = myIndexY + y[0];  i < myIndexY + y[1];  i++ ) {
					if (set1.contains(sortedElementsY[i])) {
						System.out.println(sortedElementsY[i].getXpos() +" / " + sortedElementsY[i].getYpos()); // print 10 20
					}
				}
			}
			
			private void findSortBoundaryX() {
				
//				TODO  implement a faster way to find the boundary
//				
//				int 	steps		= 4;
//				boolean positive	= false;
				
//				int negBound = 0;
//				int posBound = sortedElementsX.length;

//				for( int i = myIndexX; myPos.x - sortedElementsX[i].getXpos() <= maximumDistanceAffected && i > 0  ;i--) negBound = i;
				for( int i = prevboundMin; myPos.x - sortedElementsX[i].getXpos() >= maximumDistanceAffected && i < sortedElementsX.length-1  ;i++) prevboundMin = i;// negBound = i;
				
//				for( int i = myIndexX; sortedElementsX[i].getXpos() - myPos.x <= maximumDistanceAffected && i < sortedElementsX.length-2  ;i++) posBound = i;				
				for( int i = prevboundMax; sortedElementsX[i].getXpos() - myPos.x <= maximumDistanceAffected && i < sortedElementsX.length-1  ;i++) prevboundMax = i; //posBound = i;
				
//				prevboundMin = negBound;
//				prevboundMax = posBound;
//				
//				System.out.println(prevboundMin);
//				System.out.println(prevboundMax);
//				System.out.println();
				
//				negBound -= myIndexX;
//				posBound -= myIndexX;
				
//				return new int[] {negBound, posBound};
//				return new int[] {prevboundMin, prevboundMax};
			}
			
			private int[] findSortBoundaryY() {
				
//				TODO  implement a faster way to find the boundary
//				
//				int 	steps		= 4;
//				boolean positive	= false;
				
				int negBound = 0;
				int posBound = sortedElementsY.length;
				
//				System.out.println("LENGTH: " + sortedElementsY.length);

				for( int i = myIndexY; myPos.y - sortedElementsY[i].getYpos() <= maximumDistanceAffected && i > 0  ;i--) negBound = i;
				
				for( int i = myIndexY; sortedElementsY[i].getYpos() - myPos.y <= maximumDistanceAffected && i < sortedElementsY.length-1  ;i++) posBound = i;
				
				negBound -= myIndexY;
				posBound -= myIndexY;
				
//				System.out.println(negBound + "  " +posBound);
				
				return new int[] {negBound, posBound};
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

							int thisRelation =  myTable.getRelation( myTerm, e.getTerm());
							
							float thisInfluenceMag = 1 / (thisRelation + 0.000001f);
							
							thisInfluenceMag = thisInfluenceMag / thisDistance * influenceFactor * -1;

//  so war's mal:
//							if (thisRelation == 0) {
//								thisInfluenceMag = (float) repulsionRate / thisDistance;
////								thisInfluenceMag = 0f;
//							} else {
//
//								float thisRelationSquared = (float) (thisRelation * thisRelation);
//								//thisInfluenceMag = thisRelation;
//								thisInfluenceMag = (float) influenceFactor * thisRelationSquared / thisDistance;
//
//							}
// bis hier

							thisInfluenceDirection.mult(thisInfluenceMag);

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
				
				if( hasHistory ) {
					history.add(new PVector(myPos.x, myPos.y));
				}
				
				satisfied = false;
				
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
			
			public int getIndexX() {
				return myIndexX;
			}

			public void setIndexX(int indexX) {
				this.myIndexX = indexX;
			}

			public int getIndexY() {
				return myIndexY;
			}

			public void setIndexY(int indexY) {
				this.myIndexY = indexY;
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
		public boolean saveVisualisation(boolean _askForName, String _suggestedName) {

			if(graphics != null) {
				//System.out.println("SICHER IST SICHER");

				//				String input;
				//				if (_askForName) {
				//					input = JOptionPane.showInputDialog("please input filename", _suggestedName);
				//				}
				//				else {
				//					input = _suggestedName;
				//				}
				//				
				//				if( input == null ) { return false; }
				//				
				//				if( input != null ) {

				String s = new File(_suggestedName).getAbsolutePath()  ;

				graphics.save(s + ".jpg");
				//visApplet.save(input+".jpg");
				System.out.println("SAVED AS: "+ s);

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
			
			zoomfactor += _zoom/6 * (zoomfactor/3);
			if(zoomfactor <= 0) zoomfactor = (float)0.001;
		}

		@Override
		public void setDisplayOffset(int _xOffset, int _Yoffset) {
			xOffset += _xOffset;
			yOffset += _Yoffset;
			
		}

		public void setSave(boolean b) {
			
			save = b;
			String TMPfolderName, TMPfileName;
			
			if (save == true) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH:mm");
				Date date = new Date();
				
				
				TMPfolderName = pa.outputFolderPath +  dateFormat.format(date) + "_" + myTablesType + "_" + myVismodeString;
				TMPfileName = myTablesType + "_Nebular";
				
				String input = JOptionPane.showInputDialog("please input folder name", TMPfolderName);

				while(input == null ) {
					try {
						Thread.sleep(20);
						System.out.println("waiting");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
				if( input.equalsIgnoreCase("")) {
					
					save = false;
					pa.setSaveButtonState(false);
				} else {
					
					folderName = input;
					fileName = TMPfileName;
				}
			}
			
			
		}

		@Override
		public boolean setType(TableTypes _type) {
			if( myTablesType == null) {
				myTablesType = _type;
				return true;
			} else {
				return false;
			}
		}

		@Override
		public void destroy() {

			this.graphics = null;
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

			//visApplet.image(brush,200,200);
			
			for(int x = 0; x<noOfCells; x++ ) {
				for(int y = 0; y<noOfCells; y++) {
					
					//visApplet.fill(  230-(_table.getRelationByIndex(x, y)*10)  );
					//visApplet.rect(100+(x*(cellSize)), 100+(y*(cellSize)), cellSize, cellSize);
					this.graphics.tint(255, _table.getRelationByIndex(x, y)*2 );
					this.graphics.image(brush,40+(x*(cellSize)), 40+(y*(cellSize)));
					
				}
			}
			
			
			this.graphics.endDraw();
			//show = visApplet;
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
					graphics.save(pa.outputFolderPath+input+".jpg");
					//visApplet.save(input+".jpg");
					System.out.println("SAVED AS: "+pa.outputFolderPath+input+".jpg");
					
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

		@Override
		public boolean setType(TableTypes _type) {
			if( myTablesType != null ) {
				return false;
			}
			else {
				myTablesType = _type;
				return true;
			}
		}

		@Override
		public boolean isUpdateable() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void destroy() {

			this.graphics = null;
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

			if( graphics == null ) {
				this.graphics = createGraphics(100, 100);				
			}
			
			
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
			//show = visApplet;
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
					graphics.save(pa.outputFolderPath+input+".jpg");
					//visApplet.save(input+".jpg");
					System.out.println("SAVED AS: "+pa.outputFolderPath+input+".jpg");
					
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
			if( visBool) {
				
				PGraphics r;
				try {
					r = graphics;
					graphics = null;
				} catch (Exception e) {
					r = null;
				}
				return r;
			}
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

		@Override
		public boolean setType(TableTypes _type) {
			if( myTablesType != null ) {
				return false;
			}
			else {
				myTablesType = _type;
				return true;
			}
		}

		@Override
		public boolean isUpdateable() {
			// TODO Auto-generated method stub
			return false;
		}

		public void destroy() {
			this.graphics = null;
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
					graphics.save(pa.outputFolderPath+input+".jpg");
					//visApplet.save(input+".jpg");
					System.out.println("SAVED AS: "+pa.outputFolderPath+input+".jpg");
					
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

		@Override
		public boolean setType(TableTypes _type) {
			if( myTablesType != null ) {
				return false;
			}
			else {
				myTablesType = _type;
				return true;
			}
		}

		@Override
		public boolean isUpdateable() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void destroy() {

			this.graphics = null;
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
					//visApplet.stroke(0, 60 );
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
					graphics.save(pa.outputFolderPath+input+".jpg");
					//visApplet.save(input+".jpg");
					System.out.println("SAVED AS: "+pa.outputFolderPath+input+".jpg");
					
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

		@Override
		public boolean setType(TableTypes _type) {

			if( myTablesType != null ) {
				return false;
			}
			else {
				myTablesType = _type;
				return true;
			}
		}

		@Override
		public boolean isUpdateable() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void destroy() {

			this.graphics = null;
		}
	}

	@Override
	public boolean isUpdateable() {
		// TODO Auto-generated method stub
		return false;
	}

	

	



}