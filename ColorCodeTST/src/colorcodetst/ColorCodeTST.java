package colorcodetst;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import javax.swing.*;

import processing.core.PApplet;
import processing.data.XML;
import MyUtils.StatusGui;

public class ColorCodeTST extends PApplet implements ActionListener {
	
	private static final long serialVersionUID = 4444550612806782893L;
	
	public boolean debug = false;
	
	private JPanel				container;
	
	private Panel 				buttonPanel;
	private Button 				visualize, save_visualisation, save_all_sorted, close, quit;
	private JRadioButton		mode1, mode2, mode3;
	private ButtonGroup			group;
	private Panel				sortOptions;
	private Button				sortOptions_go;
	private JComboBox			visTable, sortOptions_focal, sortOptions_type;
	
	VisFrame visFrame;
	StatusGui stat;
	
	public enum tables { NONE, KEYS, OBJ_S, OBJ_M, SORTED };
	
	public String dataFolderPath;
	
	// depricated:
	private XMLdatabase 	flussData;
	private XMLdatabase 	exhibitsData;
	//
	
	private XML 			bodenlosOS;
	private String[] 		objectsList;
	private String[] 		keywordsList;
	private RelationTable 	keywordRelations;
	private RelationTable 	objectRelationsSimple;
	private RelationTable 	objectRelationsMeta;
	private RelationTable 	sortedTable;

	private boolean mouseMoved  = true;
	
	public void setup() {

		size(800, 800);
		colorMode(HSB);
		
		stat = new StatusGui();
		stat.update(0, "calling startupRoutine...");
		
		startupRoutine();
		stat.end();
	}
	
	void startupRoutine() {
		
		stat.update(0, "setting folder paths");
		File rootFolder = new File(".");
		dataFolderPath = rootFolder.getAbsolutePath();
		dataFolderPath = dataFolderPath.substring(0, dataFolderPath.length()-1);
		dataFolderPath = dataFolderPath.concat("src/data/");
		dprint("Data-Folder path: "+dataFolderPath);
		stat.completed();
				
		
		loadBodenlosOS();
		getObjectsList( "KEYWORDED-ONLY" );
		getKeywordsList();
		initiateRelationTables();
		parseKeywordRelationsToTable();
		parseObjectRelationsSimpleToTable();
		
		initializeButtons();

		stat.update(0, "printing keywordsRelationTable");
		keywordRelations.printTable();
		stat.completed();
		stat.update(0, "printing objectsRelationsTable");
		objectRelationsSimple.printTable();
		stat.completed();
		
		//initFlusseriana();
		//initExhibits();		
		//flussData.hasConnections("Author", "Peter Weibel", "Entry",666);
		//flussData.hasConnections("Author", "Philipp Tögel", "Entry Contagem",666);

		//println("...exit programm after successful run. being happy and all!");
		//exit();
	}
		
	void initializeButtons() {
		
		stat.update(0, "initializing buttons");

		container = new JPanel( new GridLayout(3, 1));
//		container = new JPanel( new BorderLayout(10, 15));
		container.setBorder(BorderFactory.createTitledBorder("warum nicht"));

		
		buttonPanel = new Panel();
		//buttonPanel.setBounds(0, 0, width, height);
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.setBackground(Color.lightGray);

		
		visTable = new JComboBox(new Object[]{tables.KEYS, tables.OBJ_S, tables.OBJ_M, tables.SORTED});
		visTable.addActionListener(this);
		//visTable.setLayout(null);
		buttonPanel.add(visTable);
		
		visualize = new Button("visualize");
		visualize.addActionListener(this);
		buttonPanel.add(visualize);
		
		mode1 = new JRadioButton("1");
		mode2 = new JRadioButton("2");
		mode3 = new JRadioButton("c");
		mode2.setSelected(true);
		group = new ButtonGroup();
		group.add(mode1);
		group.add(mode2);
		group.add(mode3);
		buttonPanel.add(mode1);
		buttonPanel.add(mode2);
		buttonPanel.add(mode3);
		
		save_visualisation = new Button("save visualisation");
		save_visualisation.addActionListener(this);
		buttonPanel.add(save_visualisation);
		
		save_all_sorted = new Button("save all sorted");
		save_all_sorted.addActionListener(this);
		buttonPanel.add(save_all_sorted);
		
		close = new Button("close");
		close.addActionListener(this);
		buttonPanel.add(close);
		
		quit = new Button("quit");
		quit.addActionListener(this);
		buttonPanel.add(quit);
		
		container.add(buttonPanel);
		
		
		
		sortOptions = new Panel();
		//sortOptions.setBounds(0, 0,  width, height);
		sortOptions.setLayout(new FlowLayout());
		sortOptions.setBackground(Color.MAGENTA);
		
		sortOptions_type = new JComboBox(tables.values());
		sortOptions_type.addActionListener(this);
		sortOptions.add(sortOptions_type);
		
		sortOptions_focal = new JComboBox();
		DefaultComboBoxModel modl = new DefaultComboBoxModel(new String[]{"none"});
		sortOptions_focal.setModel(modl);
		sortOptions_focal.setMaximumRowCount(40);
		sortOptions_focal.addActionListener(this);
		sortOptions.add(sortOptions_focal);
		
		sortOptions_go = new Button("go");
		sortOptions_go.addActionListener(this);
		sortOptions.add(sortOptions_go);
		
		sortOptions.setVisible(false);
		container.add(sortOptions);
		
		add(container);
		container.setVisible(true);
		
		//cardLayout.show(container, "buttonPanel");
		//container.setVisible(true);
		
		//add(container);

		
		stat.completed();
	}

	public void actionPerformed( ActionEvent e) {
		
		if( e.getSource() == visualize) {
			
			visualize();
		}
		if( e.getSource() == save_visualisation) {
			
			
			String filename;
			tables tableType = visFrame.visApplet.getCurrentTableType();
			
			switch (tableType) {
			case KEYS:
				
				filename =  "KEYS_";
				filename += visFrame.visApplet.getVismodeString();
				filename += "-";
				filename +=  keywordRelations.getFocal();
				
				visFrame.visApplet.saveVisualisation(filename);
				break;
				
			case OBJ_S:
				
				filename  = "OBJECT-S_";
				filename += visFrame.visApplet.getVismodeString();
				filename += "_";
				filename += objectRelationsSimple.getFocal();
				
				visFrame.visApplet.saveVisualisation(filename);
				break;
				
			case SORTED:
				
				filename  = ""+sortedTable.getTablesType();
				filename += "_";
				filename += visFrame.visApplet.getVismodeString();
				filename += "_";
				filename += objectRelationsSimple.getFocal();
				
				visFrame.visApplet.saveVisualisation(filename);
				break;
				
			default:
				
				break;
			}
		}
		
		if( e.getSource() == save_all_sorted) {
			
			saveAllSorted();
		}
		
		if( e.getSource() == close ) { 

			visFrame.visApplet.destroy();
			visFrame.dispose();
			visFrame = null;
		}
		if( e.getSource() == quit ) {
			exit();
			
		}
		if( e.getSource() == sortOptions_type ) {

			populateTheList();
		}
		if( e.getSource() == sortOptions_go) {
			
			String focal = sortOptions_focal.getSelectedItem().toString(); 
			tables type = (tables)sortOptions_type.getSelectedItem();
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
		if( e.getSource() == visTable) {
			
		    visTable.getSelectedItem();
			if( visTable.getSelectedItem() == tables.SORTED) {

				sortOptions.setVisible(true);
				

			}
			else {
				sortOptions.setVisible(false);
			}
			dprint("vistable changed");
		}
		
	}

	private void visualize() {
		
		if(visFrame == null ) {
			
			visFrame = new VisFrame( this, 800,800);
			visFrame.newVisualisation();
		}
		else {
			visFrame.newVisualisation();
		}		
	}


	void saveAllSorted() {

		String vt = (String)visTable.getSelectedItem();
		tables currentType;
		String[] words;
		
		if( vt.equalsIgnoreCase("KEYS") ) {
			words = keywordRelations.getRowValuesArrayAlphabetically();
			currentType = tables.KEYS;
		}
		else if ( vt.equalsIgnoreCase("OBJ_S") ) {
			words = objectRelationsSimple.getRowValuesArrayAlphabetically();
			currentType = tables.OBJ_S;
		}
		else {
			words = null;
			currentType = null;
		}

		visTable.setSelectedItem("SORTED");


		for(String currentWord : words) {

				dprint("SA: sorting...");
			sortRelationTable(currentType, currentWord);
				dprint("SA: visualizing...");
			visualize();
				dprint("SA: setting title...");
			visFrame.setTitle(currentWord);
				dprint("SY: saving...");
			visFrame.visApplet.saveVisualisation( currentType + " SORTED " + currentWord );
				dprint("SA: done");
				System.gc();
			

		}
		
	}
	
	void populateTheList() {
		
		String[] theList;
		tables visopt = (tables)sortOptions_type.getSelectedItem();

		switch (visopt) {

		case KEYS:
			theList = keywordRelations.getRowValuesArrayAlphabetically();
			break;

		case OBJ_S:
			theList = objectRelationsSimple.getRowValuesArrayAlphabetically();
			break;

		case OBJ_M:
			theList = objectRelationsMeta.getRowValuesArrayAlphabetically();
			break;

		default:
			theList = null;
			break;

		}
		// If objectRelations Tables fail (==null) -> reinstantiate!

		if(theList != null) {
			DefaultComboBoxModel modl = new DefaultComboBoxModel(theList);
			sortOptions_focal.setModel(modl);
		}
		else {
			DefaultComboBoxModel modl = new DefaultComboBoxModel(new String[] {"none"});
			sortOptions_focal.setModel(modl);
		}
	}
	
	void sortRelationTable( tables type, String focal) {

		switch (type) {
		case KEYS:
			sortedTable = new RelationTable(tables.KEYS, keywordRelations, keywordRelations.getSortedIndices(focal), focal );
			break;
		case OBJ_S:
			sortedTable = new RelationTable( tables.OBJ_S, objectRelationsSimple, objectRelationsSimple.getSortedIndices(focal), focal );
			break;
		case OBJ_M:
			sortedTable = new RelationTable( tables.OBJ_M, objectRelationsMeta, objectRelationsMeta.getSortedIndices(focal), focal );
			break;
		default:
			break;
		}
		
		//for(String a : sortedList) System.out.println(a);
		dprint("sorted Table populated");
		dprint("");
		//sortedTable.printTable();

	}
	
	void parseObjectRelationsSimpleToTable() {
		
		stat.update(0, "parsing Object Relations");
	
		
		String[] currentKeywords, testKeywords;
		String currentObjectString, testObjectString;
		
		XML[] objects = bodenlosOS.getChild("RESULTSET").getChildren();
		XML currentObjectXML, testObjectXML;
		
		for( int i = 0; i<objects.length; i++ ) {

			currentObjectXML = objects[i];
			currentKeywords = currentObjectXML.getChild(2).getContent().split(" ");
			currentObjectString = currentObjectXML.getChild(0).getContent();
			
			int totalMatches = 0;
			
			dprint(currentObjectString);
			dprint(currentKeywords.length);
			
			stat.start2();
			for(String k : currentKeywords) {
				dprint(k);
				stat.update2(k);
			}
			
			if(currentKeywords.length >0 && currentKeywords[0] != "") {
			
				dprint("YES");
				
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
					
					dprint( currentObjectString + " matches " + testObjectString +": " + matchCount + " times.");				
				}
				dprint("total matches for "+currentObjectString+": "+totalMatches);
				dprint("");
			}
		}
		
		stat.completed();
		
	}
	
	void parseKeywordRelationsToTable() {
		
		stat.update(0, "parsing Keyword Relations");
		
		String keysString;
		String[] keysArray;
		
		for(int objIdx = 0; objIdx < objectsList.length; objIdx++ ) {
			
			keysString = bodenlosOS.getChild("RESULTSET").getChild(objIdx).getChild(2).getContent();
			
			if( !keysString.equalsIgnoreCase("")   ) {
				
				keysArray = keysString.split(" ");
				
				for(String s : keysArray) dprint(s);
				
				dprint(keysArray.length);
				dprint("");		
				
				for(int i = 0; i< keysArray.length; i++) {
					
					for(int ii = i; ii<keysArray.length; ii++) {
						
						if( !keysArray[i].isEmpty() && !keysArray[ii].isEmpty() ) {
							
							keywordRelations.increaseRelation(keysArray[i], keysArray[ii], 1);
							keywordRelations.increaseRelation(keysArray[ii], keysArray[i], 1);
						}
						else {
							dprint("NO KEYWORDS FOUND FOR OBJECT AT INDEX:" + objIdx );
						}
					}
				}
			}
		}		
		stat.completed();
	}

	void initiateRelationTables() {
		stat.update(0, "initializing RelationTables");
		keywordRelations 		= new RelationTable(tables.KEYS, keywordsList);
		objectRelationsSimple 	= new RelationTable(tables.OBJ_S, objectsList);
		objectRelationsMeta 	= new RelationTable(tables.OBJ_M, objectsList);
		stat.completed();
	}

	void loadBodenlosOS() {
		stat.update(0, "try reading bodenlosOS.xml");
		try {
			
			bodenlosOS = loadXML(dataFolderPath+"bodenlosOS.xml");
			//bodenlosOS = loadXML(dataFolderPath+"testDatenbank_bodenlosOS.xml");
			stat.completed();
		} catch (Exception e) {
			stat.update(1,"ERROR LOADING XML: bodenlosOS.xml");
			e.printStackTrace();
			println();
			stat.update(1,"PROGRAMM TERMINATED.");
			exit();
		}
	}

	void getObjectsList(String _mode) {
		
		stat.update(0, "getting ObjectsList");
		
		int mode;
		
		if(_mode.equalsIgnoreCase("ALL")) mode = 0;
		else if(_mode.equalsIgnoreCase("KEYWORDED-ONLY")) mode = 1;
		else mode = 0;
		
		XML resultset = bodenlosOS.getChild("RESULTSET");
		
		//int numberOfEntries = resultset.getInt("FOUND");
		
		ArrayList<String> tempObjList = new ArrayList<String>();
		
		int count = 0;
		for(XML entry : resultset.getChildren()) {
			
			String data = entry.getChild(2).getChild(0).getContent();
			if( (mode == 1 && !data.equalsIgnoreCase("")) || (mode == 0) ) {
				
				tempObjList.add(entry.getChild(0).getChild(0).getContent());
				//objectsList[count] = entry.getChild(0).getChild(0).getContent();
				count++;
			}
		}
		
		objectsList = new String[count];
		tempObjList.toArray(objectsList);

		dprint("mode = " + mode + " "+_mode);
		dprint("counting: "+count);
		stat.completed();
	}
	
	void getKeywordsList() {
		
		stat.update(0, "getting KeywordsList");
		
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
		
		for(String g:keywordsList	) dprint(g);
		dprint("### END KEYWORDS LIST ###");
		
		stat.completed();
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
				dprint(exhibitsDataInt.get(elem) +" "+ elem);
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
			dprint(e); e.printStackTrace();
		}
	}
	
	void initFlusseriana() {
		
		XML flusseriana;
		flusseriana = loadXML(dataFolderPath+"flusseriana_exported.xml");
		dprint(flusseriana.getName());
		
		HashMap<String, Integer> flussDataInt = new HashMap<String, Integer>();
		flussData = new XMLdatabase("RESULTSET");

		

		for( String s : flusseriana.listChildren()) {

			XML child = flusseriana.getChild(s);
			dprint(s);			

			// set the column names:
			if(child.getName().equalsIgnoreCase("METADATA")) {
				Integer i=0;
				for(XML field : child.getChildren()){
					dprint(i+": "+field.getString("NAME"));
					//dprint("TYPE:      "+field.getString("TYPE"));
					//dprint("EMPTYOK:   "+field.getString("EMPTYOK"));
					//dprint("MAXREPEAT: "+field.getString("MAXREPEAT"));

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
		    
		    setTitle("visual");
		    setResizable(true);
		    //setExtendedState(JFrame.MAXIMIZED_BOTH);
		    setUndecorated(false);
		    setVisible(true);
		    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		    //setBounds((visApplet.getBounds()));
		    //show();
		    

		    	
		    	
		    //visApplet.visualize2(sortedTable);
		}
		
		public void  newVisualisation() {
			
		    tables type = (tables)visTable.getSelectedItem();
		    
		    int visModeSelector = 0;
		    if( mode1.isSelected() ) visModeSelector = 1;
		    if( mode2.isSelected() ) visModeSelector = 2;
		    if( mode3.isSelected() ) visModeSelector = 3;
			   
		    switch (type) {
			
		    case KEYS:
				if (keywordRelations != null) {
					visApplet.visualize(keywordRelations, visModeSelector);
				}
				break;
			
			case OBJ_S:
				if (objectRelationsSimple != null) {
					visApplet.visualize(objectRelationsSimple, visModeSelector);
				}
				break;
			
			case OBJ_M:
				if (objectRelationsMeta != null) {
					visApplet.visualize(objectRelationsMeta, visModeSelector);
				}
				break;
				
			case SORTED:
				if (sortedTable != null) {
					visApplet.visualize(sortedTable, visModeSelector);
				}
				break;
				
				
			default:
				break;
			}

		}
	}
	
	private void dprint(Object _p) {
		if( debug ) System.err.println(_p);
	}
}

