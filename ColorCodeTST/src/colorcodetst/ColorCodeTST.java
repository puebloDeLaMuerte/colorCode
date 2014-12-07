package colorcodetst;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.TableStringConverter;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.data.XML;
import MyUtils.StatusGui;
import MyUtils.VisModes;
import MyUtils.TableTypes;

public class ColorCodeTST extends PApplet implements ActionListener {
	
	private static final long serialVersionUID = 4444550612806782893L;
	
	public boolean debug = true;
	
	private JPanel				container;
	private Panel 				subContainer;
	
	private Panel 				visPanel, visModePanel, savePanel, exitPanel, sortOptions;
	private Button 				visualize, save_visualisation, save_all_sorted, close, quit;
	private JRadioButton		mode1, mode2, mode3, mode4, mode5;
	private ButtonGroup			group;
	private Button				sortOptions_go, sortOptions_path;
	private JComboBox			visTable, sortOptions_focal, sortOptions_type;
	
	VisFrame visFrame;
	VisFrame nebulaFrame;
	
	StatusGui stat;
	
	public VisModes currentVisMode;
			
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

		subContainer = new Panel();
		subContainer.setLayout(new FlowLayout());
		
//		container = new JPanel( new GridLayout(0, 1));
		container = new JPanel( new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		container.setBackground(Color.LIGHT_GRAY);
		//container.setBorder(BorderFactory.createTitledBorder("warum nicht"));
		//container.setOpaque(true);
		
		visTable = new JComboBox(new TableTypes[]{TableTypes.KEYS, TableTypes.OBJ_S, TableTypes.OBJ_M, TableTypes.SORTED});
		visTable.addActionListener(this);
		
		visualize = new Button("visualize");
		visualize.addActionListener(this);
		
		mode1 = new JRadioButton("grid1");
		mode2 = new JRadioButton("grid2");
		mode3 = new JRadioButton("circ");
		mode4 = new JRadioButton("path");
		mode5 = new JRadioButton("nebular");	
		mode1.addActionListener(this);
		mode2.addActionListener(this);
		mode3.addActionListener(this);
		mode4.addActionListener(this);
		mode5.addActionListener(this);
		mode2.setSelected(true);
		group = new ButtonGroup();
		group.add(mode1);
		group.add(mode2);
		group.add(mode3);
		group.add(mode4);
		group.add(mode5);
		
		save_visualisation = new Button("save visualisation");
		save_visualisation.addActionListener(this);
		
		save_all_sorted = new Button("save all sorted");
		save_all_sorted.addActionListener(this);
		
		close = new Button("close");
		close.addActionListener(this);
		
		quit = new Button("quit");
		quit.addActionListener(this);
		
		visPanel = new Panel();
		visPanel.setLayout(new GridLayout(1, 2));
		visPanel.add(visTable);
		visPanel.add(visualize);
		gbc.gridx = 0;
		gbc.gridy = 0;
		container.add(visPanel, gbc);
		
		visModePanel = new Panel();
		visModePanel.setLayout(new GridLayout(1,4));
		visModePanel.add(mode1);
		visModePanel.add(mode2);
		visModePanel.add(mode3);
		visModePanel.add(mode4);
		visModePanel.add(mode5);
		gbc.gridx = 0;
		gbc.gridy = 1;
		container.add(visModePanel, gbc);
		
		savePanel = new Panel();
		savePanel.setLayout(new GridLayout(1,2));
		savePanel.add(save_visualisation);
		savePanel.add(save_all_sorted);
		gbc.gridx = 0;
		gbc.gridy = 2;
		container.add(savePanel, gbc);
		
		exitPanel = new Panel();
		exitPanel.setLayout(new GridLayout(1,2));
		exitPanel.add(close);
		exitPanel.add(quit);
		gbc.gridx = 0;
		gbc.gridy = 3;
		container.add(exitPanel, gbc);
		
		
		sortOptions = new Panel();
		//sortOptions.setBounds(0, 0,  width, height);
		sortOptions.setLayout(new FlowLayout());
		sortOptions.setBackground(Color.MAGENTA);
		
		sortOptions_type = new JComboBox(TableTypes.values());
		sortOptions_type.addActionListener(this);
		sortOptions.add(sortOptions_type);
		
		sortOptions_focal = new JComboBox();
		DefaultComboBoxModel modl = new DefaultComboBoxModel(new String[]{"none"});
		sortOptions_focal.setModel(modl);
		sortOptions_focal.setMaximumRowCount(40);
		sortOptions_focal.addActionListener(this);
		sortOptions.add(sortOptions_focal);
		
		sortOptions_go = new Button("sort");
		sortOptions_go.addActionListener(this);
		sortOptions.add(sortOptions_go);
		
		sortOptions_path = new Button("calculate path");
		sortOptions_path.addActionListener(this);
		sortOptions.add(sortOptions_path);
		
		sortOptions.setVisible(true);
		gbc.gridx = 0;
		gbc.gridy = 4;
		sortOptions_focal.setEnabled(false);
		sortOptions_type.setEnabled(false);
		sortOptions.setEnabled(false);
		sortOptions.setBackground(Color.LIGHT_GRAY);
		container.add(sortOptions, gbc);

		container.setVisible(true);
		add(subContainer);
		
		subContainer.add(container);
		//subContainer.setBorder(BorderFactory.createLineBorder(Color.BLACK)); 
		subContainer.setBackground(Color.LIGHT_GRAY);
		subContainer.setCursor(new Cursor(Cursor.HAND_CURSOR));
		subContainer.setVisible(true);

		
		stat.completed();
	}

	public void actionPerformed( ActionEvent e) {
		
		if( e.getSource() == visualize) {
			
			visualize();
		}
		if( e.getSource() == save_visualisation) {
			
			
			String filename;
			TableTypes tableType = visFrame.visApplet.getCurrentTableType();
			
			switch (tableType) {
			case KEYS:
				
				filename =  "KEYS_";
				filename += visFrame.visApplet.getVismodeString();
				filename += "-";
				filename +=  keywordRelations.getFocal();
				
				visFrame.visApplet.saveVisualisation(true, filename);
				break;
				
			case OBJ_S:
				
				filename  = "OBJECT-S_";
				filename += visFrame.visApplet.getVismodeString();
				filename += "_";
				filename += objectRelationsSimple.getFocal();
				
				visFrame.visApplet.saveVisualisation(true, filename);
				break;
				
			case SORTED:
				
				filename  = ""+sortedTable.getTablesType();
				filename += "_";
				filename += visFrame.visApplet.getVismodeString();
				filename += "_";
				filename += objectRelationsSimple.getFocal();
				
				visFrame.visApplet.saveVisualisation(true, filename);
				break;
				
			default:
				
				break;
			}
		}
		
		if( e.getSource() == save_all_sorted) {
			
			saveAllSorted();
		}
		
		if( e.getSource() == close ) { 

			closeVisFrame();
		}
		if( e.getSource() == quit ) {
			exit();
			
		}
		if( e.getSource() == sortOptions_type ) {

			populateTheList();
		}
		if( e.getSource() == sortOptions_go || e.getSource() == sortOptions_path) {
			
			doSort(e);
		}
		if( e.getSource() == visTable) {
			
		    visTable.getSelectedItem();
			if( visTable.getSelectedItem() == TableTypes.SORTED) {

				sortOptions_focal.setEnabled(true);
				sortOptions_type.setEnabled(true);
				sortOptions.setEnabled(true);
				sortOptions.setBackground(Color.PINK);
				

			}
			else {
				
				sortOptions_focal.setEnabled(false);
				sortOptions_type.setEnabled(false);
				sortOptions.setEnabled(false);
				sortOptions.setBackground(Color.LIGHT_GRAY);
			}
			dprint("vistable changed");
		}
		if( e.getSource() == mode1 ) {
			currentVisMode = VisModes.GRID_HARD;
		}
		if( e.getSource() == mode2 ) {
			currentVisMode = VisModes.GRID_SOFT;
		}
		if( e.getSource() == mode3 ) {
			currentVisMode = VisModes.CIRCULAR;
		}
		if( e.getSource() == mode4 ) {
			currentVisMode = VisModes.PATH;
		}
		if( e.getSource() == mode5 ) {
			currentVisMode = VisModes.NEBULAR;
		}

		
	}
	
	public void closeVisFrame() {
		visFrame.visApplet.destroy();
		visFrame.dispose();
		visFrame = null;		
	}

	private void doSort( ActionEvent e ) {
		
		String focal = sortOptions_focal.getSelectedItem().toString(); 
		TableTypes type = (TableTypes)sortOptions_type.getSelectedItem();
		
		if( !focal.equalsIgnoreCase("none") ) {
			
			if( e.getSource() == sortOptions_go)    sortRelationTable(type, focal);
			if( e.getSource() == sortOptions_path)  {
				
				switch (type) {
				case KEYS:
					keywordRelations.findPathForFocal(focal, 1000);
					break;
				case OBJ_S:
					objectRelationsSimple.findPathForFocal(focal, 1000);
				case SORTED:
					sortedTable.findPathForFocal(focal, 1000);
				default:
					break;
				}
				
			}
		}
		else {
			JOptionPane.showMessageDialog(frame,
				    "no focal point selected!",
				    "error",
				    JOptionPane.WARNING_MESSAGE);
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

		TableTypes vt = (TableTypes)visTable.getSelectedItem();
		
		TableTypes currentType;
		String[] words;
		
		if( vt == TableTypes.KEYS ) {
			words = keywordRelations.getRowValuesArrayAlphabetically();
			currentType = TableTypes.KEYS;
		}
		else if ( vt == TableTypes.OBJ_S ) {
			words = objectRelationsSimple.getRowValuesArrayAlphabetically();
			currentType = TableTypes.OBJ_S;
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
			boolean saved = visFrame.visApplet.saveVisualisation( false, currentType + " SORTED " + currentWord );
				dprint("SA: done");
				System.gc();
			if( !saved ) break; 
		}
		
	}
	
	void populateTheList() {
		
		String[] theList;
		TableTypes visopt = (TableTypes)sortOptions_type.getSelectedItem();

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
	
	void sortRelationTable( TableTypes type, String focal) {

		switch (type) {
		case KEYS:
			sortedTable = new RelationTable( this, TableTypes.KEYS, keywordRelations, keywordRelations.getSortedIndices(focal), focal );
			break;
		case OBJ_S:
			sortedTable = new RelationTable( this, TableTypes.OBJ_S, objectRelationsSimple, objectRelationsSimple.getSortedIndices(focal), focal );
			break;
		case OBJ_M:
			sortedTable = new RelationTable( this, TableTypes.OBJ_M, objectRelationsMeta, objectRelationsMeta.getSortedIndices(focal), focal );
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
		keywordRelations 		= new RelationTable(this, TableTypes.KEYS, keywordsList);
		objectRelationsSimple 	= new RelationTable(this, TableTypes.OBJ_S, objectsList);
		objectRelationsMeta 	= new RelationTable(this, TableTypes.OBJ_M, objectsList);
		stat.completed();
	}

	void loadBodenlosOS() {
		stat.update(0, "try reading bodenlosOS.xml");
		
		//JOptionPane.showOptionDialog(this, "load previous dataset?", "choose!", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"YES", "NO"}, 0);

		
		
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

		//public Vis visApplet;
		//public Neb nebularApplet;
		public VisApplet visApplet;
		
		public VisFrame(ColorCodeTST tempParent, int xSize, int ySize) {
			
			setBounds(0, 0, xSize, ySize);
		    setTitle("visual");
		    setResizable(true);
		    setUndecorated(false);
		    setVisible(true);
		    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		    
			visApplet = new VisApplet();

			visApplet.setJFrame(this);
			visApplet.setParent(tempParent);
			visApplet.init();
			add(visApplet);
		}
		
		public void  newVisualisation() {
			
			visApplet.setVisualisationParams(currentVisMode);

			TableTypes type = (TableTypes)visTable.getSelectedItem();
			   
		    switch (type) {
			
		    case KEYS:
				if (keywordRelations != null) {
					visApplet.visualize(keywordRelations);
				}
				break;
			
			case OBJ_S:
				if (objectRelationsSimple != null) {
					visApplet.visualize(objectRelationsSimple);
				}
				break;
			
			case OBJ_M:
				if (objectRelationsMeta != null) {
					visApplet.visualize(objectRelationsMeta);
				}
				break;
				
			case SORTED:
				if (sortedTable != null) {
					visApplet.visualize(sortedTable);
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

