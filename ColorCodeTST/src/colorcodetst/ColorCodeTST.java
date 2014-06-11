package colorcodetst;

import java.awt.Button;
import java.awt.Panel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import javax.swing.*;
import processing.core.PApplet;
import processing.data.XML;

public class ColorCodeTST extends PApplet implements ActionListener {
	
	private static final long serialVersionUID = 4444550612806782893L;
	
	private Panel 			buttonPanel;
	private Button 			visualize, save_visualisation, close, quit;
	private JRadioButton	mode1, mode2;
	private ButtonGroup		group;
	private Panel			visOptions;
	private Button			visOptions_go;
	private JComboBox		visTable, visOptions_focal, visOptions_type;
	
	VisFrame visFrame;
	
	public enum tables { NONE, KEYS, OBJ_S, OBJ_M };
	
	public String dataFolderPath;
	
	XMLdatabase flussData;
	XMLdatabase exhibitsData;
	
	XML bodenlosOS;
	String[] objectsList;
	String[] keywordsList;
	RelationTable keywordRelations;
	RelationTable objectRelationsSimple;
	RelationTable objectRelationsMeta;
	RelationTable sortedTable;

	private boolean mouseMoved  = true;
	
	public void setup() {

		size(800, 800);
		colorMode(HSB);
		

		File rootFolder = new File(".");
		dataFolderPath = rootFolder.getAbsolutePath();
		dataFolderPath = dataFolderPath.substring(0, dataFolderPath.length()-1);
		dataFolderPath = dataFolderPath.concat("src/data/");
		System.out.println("Data-Folder path: "+dataFolderPath);
		
				
		
		loadBodenlosOS();
		getObjectsList();
		getKeywordsList();
		initiateRelationTables();
		parseKeywordRelations();
		parseObjectRelationsSimple();
		
		initializeButtons();

		keywordRelations.printTable();
		objectRelationsSimple.printTable();
		
		//initFlusseriana();
		//initExhibits();		
		//flussData.hasConnections("Author", "Peter Weibel", "Entry",666);
		//flussData.hasConnections("Author", "Philipp Tögel", "Entry Contagem",666);

		//println("...exit programm after successful run. being happy and all!");
		//exit();
	}
	
		
	void initializeButtons() {
		
		buttonPanel = new Panel();
		buttonPanel.setBounds(0, 0, width, height);
		
		visTable = new JComboBox(new Object[]{"KEYS", "OBJ_S", "OBJ_M", "SORTED"});
		buttonPanel.add(visTable);
		
		visualize = new Button("visualize");
		visualize.addActionListener(this);
		buttonPanel.add(visualize);
		
		mode1 = new JRadioButton("1");
		mode2 = new JRadioButton("2");
		mode2.setSelected(true);
		group = new ButtonGroup();
		group.add(mode1);
		group.add(mode2);
		buttonPanel.add(mode1);
		buttonPanel.add(mode2);
		
		save_visualisation = new Button("save visualisation");
		save_visualisation.addActionListener(this);
		buttonPanel.add(save_visualisation);
		
		close = new Button("close");
		close.addActionListener(this);
		buttonPanel.add(close);
		
		quit = new Button("quit");
		quit.addActionListener(this);
		buttonPanel.add(quit);
		
		add(buttonPanel);
		
		visOptions = new Panel();
		visOptions.setBounds(0, 0,  width, height);
		
		visOptions_type = new JComboBox(tables.values());
		visOptions_type.addActionListener(this);
		visOptions.add(visOptions_type);
		
		visOptions_focal = new JComboBox();
		DefaultComboBoxModel modl = new DefaultComboBoxModel(new String[]{"none"});
		visOptions_focal.setModel(modl);
		visOptions_focal.setMaximumRowCount(40);
		visOptions_focal.addActionListener(this);
		visOptions.add(visOptions_focal);
		
		visOptions_go = new Button("go");
		visOptions_go.addActionListener(this);
		visOptions.add(visOptions_go);
		
		add(visOptions);
		
	}

	public void actionPerformed( ActionEvent e) {
		
		if( e.getSource() == visualize) {
			
			if(visFrame == null ) {
				
				visFrame = new VisFrame( this, 300,300);
				visFrame.newVisualisation();
			}
			else {
				visFrame.newVisualisation();
			}
		}
		if( e.getSource() == save_visualisation) {
			
			visFrame.visApplet.saveVisualisation();
			
		}
		if( e.getSource() == close ) { 

			visFrame.visApplet.destroy();
			visFrame.dispose();
			visFrame = null;
		}
		if( e.getSource() == quit ) {
			exit();
			
		}
		if( e.getSource() == visOptions_type ) {
						
			String[] theList;
			if(visOptions_type.getSelectedItem() == tables.KEYS) {
				theList = keywordRelations.getValuesArrayAlphabetically();
			}
			else if(visOptions_type.getSelectedItem() == tables.OBJ_S) {
				theList = objectRelationsSimple.getValuesArrayAlphabetically();

			}
			else if(visOptions_type.getSelectedItem() == tables.OBJ_M) {
				theList = objectRelationsMeta.getValuesArrayAlphabetically();
			}
			else theList = null;
			
			if(theList != null) {
				DefaultComboBoxModel modl = new DefaultComboBoxModel(theList);
				visOptions_focal.setModel(modl);
			}
			else {
				DefaultComboBoxModel modl = new DefaultComboBoxModel(new String[] {"none"});
				visOptions_focal.setModel(modl);
			}
		}
		if( e.getSource() == visOptions_go) {
			
			String focal = visOptions_focal.getSelectedItem().toString(); 
			tables type = (tables)visOptions_type.getSelectedItem();
			if( !focal.equalsIgnoreCase("none") ) {
				sortRelationTable(type, focal);
			}
			else {
				JOptionPane.showMessageDialog(frame,
					    "no focal point selected!",
					    "error",
					    JOptionPane.WARNING_MESSAGE);
			}
		}
		
	}
	
	void sortRelationTable( tables type, String focal) {

		switch (type) {
		case KEYS:
			sortedTable = new RelationTable(keywordRelations, keywordRelations.getSortedIndices(focal), focal );
			break;
		case OBJ_S:
			sortedTable = new RelationTable( objectRelationsSimple, objectRelationsSimple.getSortedIndices(focal), focal );
			break;
		case OBJ_M:
			sortedTable = new RelationTable( objectRelationsMeta, objectRelationsMeta.getSortedIndices(focal), focal );
			break;
		default:
			break;
		}
		
		//for(String a : sortedList) System.out.println(a);
		System.out.println("sorted Table populated");
		System.out.println();
		sortedTable.printTable();

	}
	
	void parseObjectRelationsSimple() {
		
		String[] currentKeywords, testKeywords;
		String currentObjectString, testObjectString;
		
		XML[] objects = bodenlosOS.getChild("RESULTSET").getChildren();
		XML currentObjectXML, testObjectXML;
		
		for( int i = 0; i<objects.length; i++ ) {

			currentObjectXML = objects[i];
			currentKeywords = currentObjectXML.getChild(2).getContent().split(" ");
			currentObjectString = currentObjectXML.getChild(0).getContent();
			
			int totalMatches = 0;
			
			System.out.println(currentObjectString);
			System.out.println(currentKeywords.length);
			for(String k : currentKeywords) System.out.println(k);
			
			if(currentKeywords.length >0 && currentKeywords[0] != "") {
			
				System.out.println("YES");
				
				for( int ii = i+1; ii<objects.length; ii++ ){

					testObjectXML = objects[ii];
					testObjectString = testObjectXML.getChild(0).getContent();
					testKeywords = testObjectXML.getChild(2).getContent().split(" ");

					int matchCount = 0;

					if(testKeywords.length>0 && testKeywords[0] != "") {
						for(String test : testKeywords) {
							for(String curr : currentKeywords) {

								if( test.equalsIgnoreCase(curr) ) {
									matchCount++;
									totalMatches++;
								}
							}
						}
					}
					
					objectRelationsSimple.increaseRelation(currentObjectString, testObjectString, matchCount);
					objectRelationsSimple.increaseRelation(testObjectString, currentObjectString, matchCount);
					
					System.out.println( currentObjectString + " matches " + testObjectString +": " + matchCount + " times.");				
				}
				System.out.println("total matches for "+currentObjectString+": "+totalMatches);
				System.out.println();
			}
		}
		
		
		
	}
	
	void parseKeywordRelations() {
		
		String keysString;
		String[] keysArray;
		
		for(int objIdx = 0; objIdx < objectsList.length; objIdx++ ) {
			
			keysString = bodenlosOS.getChild("RESULTSET").getChild(objIdx).getChild(2).getContent();
			
			if( !keysString.equalsIgnoreCase("")   ) {
				
				keysArray = keysString.split(" ");
				
				for(String s : keysArray) System.out.println(s);
				System.out.println(keysArray.length);
				System.out.println();
				
				for(int i = 0; i< keysArray.length; i++) {
					
					for(int ii = i; ii<keysArray.length; ii++) {
						
						if( !keysArray[i].isEmpty() && !keysArray[ii].isEmpty() ) {
							
							keywordRelations.increaseRelation(keysArray[i], keysArray[ii], 1);
							keywordRelations.increaseRelation(keysArray[ii], keysArray[i], 1);
						}
						else {
							System.out.println("NO KEYWORDS FOUND FOR OBJECT AT INDEX:" + objIdx );
						}
					}
				}
			}
		}		
	}

	void initiateRelationTables() {
		
		keywordRelations 		= new RelationTable(keywordsList);
		objectRelationsSimple 	= new RelationTable(objectsList);
		objectRelationsMeta 	= new RelationTable(objectsList);		
	}

	void loadBodenlosOS() {
		try {
			
			//bodenlosOS = loadXML(dataFolderPath+"bodenlosOS.xml");
			bodenlosOS = loadXML(dataFolderPath+"testDatenbank_bodenlosOS.xml");
			
		} catch (Exception e) {
			System.err.println("ERROR LOADING XML: bodenlosOS.xml");
			e.printStackTrace();
			println();
			System.err.println("PROGRAMM TERMINATED.");
			exit();
		}
	}

	void getObjectsList() {
		
		XML resultset = bodenlosOS.getChild("RESULTSET");
		
		int numberOfEntries = resultset.getInt("FOUND");
		objectsList = new String[numberOfEntries];
		
		int count = 0;
		for(XML entry : resultset.getChildren()) {
			objectsList[count] = entry.getChild(0).getChild(0).getContent();
			count++;
		}
	}
	
	void getKeywordsList() {
		
		XML resultset = bodenlosOS.getChild("RESULTSET");
		ArrayList<String> keys = new ArrayList<String>();
		
		for(XML entry : resultset.getChildren()) {
			
			String keywords = entry.getChild(2).getChild(0).getContent();
			
			if( !keywords.equalsIgnoreCase("") ) {
				for(String key : keywords.split(" ")) {
					if( !key.isEmpty() ) {
						keys.add(key);
					}
				}
			}	
		}
		Collection<String> noDups = new HashSet<String>(keys);
		keywordsList = noDups.toArray(new String[0]);	
		for(String g:keywordsList	) System.out.println(g);
		System.out.println("### END KEYWORDS LIST ###");
	}
	
	void initExhibits() {
		try {
			
			HashMap<String, Integer> exhibitsDataInt = new HashMap<String, Integer>();
			exhibitsData = new XMLdatabase("RESULTSET");


			FileInputStream fis = new FileInputStream(dataFolderPath.concat("Flusser_and_the _Arts_llist of works.csv"));
			InputStreamReader isr = new InputStreamReader(fis, "UTF8");
			BufferedReader CSVFile = new BufferedReader(isr);
			
			
			//first data row ist: collumns-names. fill the Hashmap with it
			String dataRow = CSVFile.readLine();
			String[] dataArray = dataRow.split(",");
			int i = 0;
			for(String elem:dataArray) {
				elem = (String)elem.subSequence(1, elem.length()-1);  // Do this to get rid of the Anführungszeichen
				exhibitsDataInt.put(elem, i++);
				System.out.println(exhibitsDataInt.get(elem) +" "+ elem);
			}
			exhibitsData.initDataInt(exhibitsDataInt);

			
			//now, fill the XML with the data from baruch...
			while (dataRow != null){
				
				XML row = new XML("ROW");
				dataArray = dataRow.split(",");
				
				for (String item:dataArray) { 
					if(item.length()>2) item = (String)item.subSequence(1, item.length()-1);  // Do this to get rid of the Anführungszeichen
					XML col = new XML("COL");
					XML dat = new XML("DATA");
					dat.setContent(item);
					col.addChild(dat);
					row.addChild(col);
				}
				exhibitsData.addChild(row);
				dataRow = CSVFile.readLine();
			}
			CSVFile.close();

		} catch(Exception e) {
			System.err.println(e); e.printStackTrace();
		}
	}
	
	void initFlusseriana() {
		
		XML flusseriana;
		flusseriana = loadXML(dataFolderPath+"flusseriana_exported.xml");
		System.out.println(flusseriana.getName());
		
		HashMap<String, Integer> flussDataInt = new HashMap<String, Integer>();
		flussData = new XMLdatabase("RESULTSET");

		

		for( String s : flusseriana.listChildren()) {

			XML child = flusseriana.getChild(s);
			System.out.println(s);			

			// set the column names:
			if(child.getName().equalsIgnoreCase("METADATA")) {
				Integer i=0;
				for(XML field : child.getChildren()){
					System.out.println(i+": "+field.getString("NAME"));
					//System.out.println("TYPE:      "+field.getString("TYPE"));
					//System.out.println("EMPTYOK:   "+field.getString("EMPTYOK"));
					//System.out.println("MAXREPEAT: "+field.getString("MAXREPEAT"));

					flussDataInt.put(field.getString("NAME"), i);
					i++;
				}
				flussData.initDataInt(flussDataInt);
			}

			// now fill flussData with data from file:
			if(child.getName().equalsIgnoreCase("RESULTSET")) {
				for(XML row : child.getChildren()) {
					flussData.addChild(row);
				}
			}
		}
	}

	public void draw() {

		if(	mouseMoved ) {
			loadPixels();
			for(int i = 0; i < this.width; i++) {
				for(int ii = 0; ii < this.width; ii++) {

					int c = ((int)map(i,0,width,80,125) + (int)map(ii,0,height,mouseX/5,mouseY/5)) % 255;
					int col = color( c, 255,255 );

					pixels[i+(width*ii)] = col;

				}
			}
			updatePixels();
		}
		mouseMoved = false;
	}
	
	public void mouseMoved() {
		mouseMoved = true;
	}


	public class VisFrame extends JFrame {

		private static final long serialVersionUID = 4258553798509737837L;

		public Vis visApplet;
		
		public VisFrame(ColorCodeTST tempParent, int xSize, int ySize) {
			
			
			
			
			setBounds(0, 0, xSize, ySize);
			//JComponent f =  (JComponent)getContentPane();
		    //f.setBorder(BorderFactory.createBevelBorder(0));
 		    
		    visApplet = new Vis();
		    add(visApplet);
		    visApplet.init();
		    visApplet.setJFrame(this);
		    visApplet.setParent(tempParent);
		    //visApplet.visualize1(keywordRelations);
		    
		    setTitle("omas suppenküche");
		    setResizable(true);
		    //setExtendedState(JFrame.MAXIMIZED_BOTH);
		    setUndecorated(true);
		    setVisible(true);
		    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		    //setBounds((visApplet.getBounds()));
		    //show();
		    

		    	
		    	
		    //visApplet.visualize2(sortedTable);
		}
		
		public void  newVisualisation() {
			
		    String s = (String)visTable.getSelectedItem();
		    
		    int visModeSelector = 0;
		    if( mode1.isSelected() ) visModeSelector = 1;
		    if( mode2.isSelected() ) visModeSelector = 2;
			   
		    
		    if(s.equalsIgnoreCase("KEYS")) 	{
		    	if (keywordRelations != null) {
					visApplet.visualize(keywordRelations, visModeSelector);
				}
		    }

		    if(s.equalsIgnoreCase("OBJ_S"))	 {
		    	if (objectRelationsSimple != null) {
					visApplet.visualize(objectRelationsSimple, visModeSelector);
				}
		    }

		    if(s.equalsIgnoreCase("OBJ_M"))		{
		    	if (objectRelationsMeta != null) {
					visApplet.visualize(objectRelationsMeta, visModeSelector);
				}
		    }

		    if(s.equalsIgnoreCase("SORTED")){
		    	if (sortedTable != null) {
					visApplet.visualize(sortedTable, visModeSelector);
				}
		    }
		}
	}

}

