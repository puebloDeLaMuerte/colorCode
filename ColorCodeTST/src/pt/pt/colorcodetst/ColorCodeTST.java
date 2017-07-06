package pt.pt.colorcodetst;

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
import pt.pt.colorcode.utils.SortOptions;
import pt.pt.colorcode.utils.SortTableType;
import pt.pt.colorcode.utils.StatusGui;
import pt.pt.colorcode.utils.TableTypes;
import pt.pt.colorcode.utils.VisModes;

public class ColorCodeTST extends PApplet implements ActionListener {
	
	private static final long serialVersionUID = 4444550612806782893L;
	
	public boolean debug = true;
	
	private MenuBar mb;
	private Menu mn1, mn2;
	private MenuItem itm1, itm2, itm3, itm4, itm5;
	
	private JPanel				container;
	private Panel 				subContainer;
	
	private Panel 				visPanel, visModePanel, savePanel, exitPanel, sortOptions;
	private Button 				visualize, save_visualisation, save_all_sorted, close, quit, adcsort;
	private JRadioButton		mode1, mode2, mode3, mode4, mode5;
	private ButtonGroup			group;
	private Button				sortOptions_go, sortOptions_path;
	private JComboBox			visTable, sortOptions_rows, sortOptions_cols, sortOptions_table, sortOptions_rowFocals, sortOptions_colFocals;
	
	VisFrame visFrame;
	VisFrame nebulaFrame;
	
	StatusGui stat;
	
	public VisModes currentVisMode;
			
	public String dataFolderPath;
	public String outputFolderPath;
	
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
		
		outputFolderPath = rootFolder.getAbsolutePath();
		outputFolderPath = outputFolderPath.substring(0, outputFolderPath.length()-1);
		outputFolderPath = outputFolderPath.concat("output/");
		dprint("Output-Folder path: "+outputFolderPath);
		
		stat.completed();
				
		
		loadBodenlosOS();
		getObjectsList( "KEYWORDED-ONLY" );
		getKeywordsList();
		initiateRelationTables();
		parseKeywordRelationsToTable();
		parseObjectRelationsSimpleToTable();
		
		initializeButtons();
		initializeMenus();

		stat.update(0, "printing keywordsRelationTable");
		keywordRelations.printTable();
		stat.completed();
		stat.update(0, "printing objectsRelationsTable");
		objectRelationsSimple.printTable();
		stat.completed();
		
		//initFlusseriana();
		//initExhibits();		
		//flussData.hasConnections("Author", "Peter Weibel", "Entry",666);
		//flussData.hasConnections("Author", "Philipp T�gel", "Entry Contagem",666);

		//println("...exit programm after successful run. being happy and all!");
		//exit();
	}
		
	private void initializeMenus() {
		
		mb = new MenuBar();
		mn1 = new Menu("Datei");
		itm1 = new MenuItem("�ffnen");
		itm2 = new MenuItem("speichern unter...");
		itm3 = new MenuItem("m�glich, aber nicht n�tig");
		mn1.add(itm1);
		mn1.add(itm2);
		mn1.addSeparator();
		mn1.add(itm3);
		
		mb.add(mn1);
		
		
		
		this.frame.setMenuBar(mb);
	}
	
	void initializeButtons() {
		
		stat.update(0, "initializing buttons");

		subContainer = new Panel();
		subContainer.setLayout(new FlowLayout());
		
//		container = new JPanel( new GridLayout(0, 1));
		container = new JPanel( new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		container.setBackground(new Color(134, 184, 204));
		//container.setBorder(BorderFactory.createTitledBorder("warum nicht"));
		//container.setOpaque(true);
		
		visTable = new JComboBox(new TableTypes[]{TableTypes.KEYS, TableTypes.OBJ_S, TableTypes.OBJ_M, TableTypes.SORTED});
		visTable.addActionListener(this);
		
		visualize = new Button("visualize");
		visualize.addActionListener(this);
		visualize.setFocusable(false);
		
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
		
		mode1.setFocusable(false);
		mode2.setFocusable(false);
		mode3.setFocusable(false);
		mode4.setFocusable(false);
		mode5.setFocusable(false);
		
//		mode1.setSelected(true);
		
		group = new ButtonGroup();
		group.add(mode1);
		group.add(mode2);
		group.add(mode3);
		group.add(mode4);
		group.add(mode5);
		
		save_visualisation = new Button("save visualisation");
		save_visualisation.addActionListener(this);
		save_visualisation.setFocusable(false);
		
		save_all_sorted = new Button("save all sorted");
		save_all_sorted.addActionListener(this);
		save_all_sorted.setFocusable(false);
		
		close = new Button("close");
		close.addActionListener(this);
		close.setFocusable(false);
		
		quit = new Button("quit");
		quit.addActionListener(this);
		quit.setFocusable(false);
		
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
		sortOptions.setLayout(new GridBagLayout());
		sortOptions.setBackground(Color.MAGENTA);
		
		JLabel what = new JLabel("table");
		what.setForeground(Color.DARK_GRAY);
		JLabel r = new JLabel("sort rows");
		r.setForeground(Color.DARK_GRAY);
		JLabel c = new JLabel("sort cols");
		c.setForeground(Color.DARK_GRAY);
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		sortOptions.add(what, gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		sortOptions.add(r, gbc);
		gbc.gridx = 2;
		gbc.gridy = 0;
		sortOptions.add(c, gbc);
		
		sortOptions_table = new JComboBox(pt.pt.colorcode.utils.SortTableType.values());
		sortOptions_table.addActionListener(this);
		sortOptions_table.setFocusable(false);
		gbc.gridx = 0;
		gbc.gridy = 1;
		sortOptions.add(sortOptions_table, gbc);
		
		sortOptions_rows = new JComboBox();
		DefaultComboBoxModel modl = new DefaultComboBoxModel(SortOptions.values());		
		sortOptions_rows.setModel(modl);
//		sortOptions_rows.setMaximumRowCount(40);
		sortOptions_rows.setFocusable(false);
		sortOptions_rows.addActionListener(this);
		sortOptions_rows.setEnabled(false);
		gbc.gridx = 1;
		gbc.gridy = 1;
		sortOptions.add(sortOptions_rows, gbc);
		
		sortOptions_cols = new JComboBox();
		DefaultComboBoxModel modl_r = new DefaultComboBoxModel(SortOptions.values());
		sortOptions_cols.setModel(modl_r);
//		sortOptions_cols.setMaximumRowCount(40);
		sortOptions_cols.setFocusable(false);
		sortOptions_cols.addActionListener(this);
		sortOptions_cols.setEnabled(false);
		gbc.gridx = 2;
		gbc.gridy = 1;
		sortOptions.add(sortOptions_cols, gbc);
		
		sortOptions_go = new Button("sort");
		sortOptions_go.addActionListener(this);
		sortOptions_go.setFocusable(false);
		sortOptions_go.setEnabled(false);
		gbc.gridx = 3;
		gbc.gridy = 1;
		sortOptions.add(sortOptions_go, gbc);
		
		
		sortOptions_rowFocals = new JComboBox();
		DefaultComboBoxModel rowmodel = new DefaultComboBoxModel(new String[]{"focal        "});
		sortOptions_rowFocals.setModel(rowmodel);
//		sortOptions_rowFocals.setMinimumSize(new Dimension(60, 1));
		sortOptions_rowFocals.setMaximumRowCount(40);
//		sortOptions_rowFocals.addActionListener(this);
		sortOptions_rowFocals.setFocusable(false);
		sortOptions_rowFocals.setEnabled(false);
		gbc.gridx = 1;
		gbc.gridy = 2;
		sortOptions.add(sortOptions_rowFocals, gbc);
		
		sortOptions_colFocals = new JComboBox();
		DefaultComboBoxModel colmodel = new DefaultComboBoxModel(new String[]{"focal        "});
		sortOptions_colFocals.setModel(colmodel);
//		sortOptions_colFocals.setMinimumSize(new Dimension(60, 1));
//		sortOptions_colFocals.
		sortOptions_colFocals.setMaximumRowCount(40);
//		sortOptions_colFocals.addActionListener(this);
		sortOptions_colFocals.setFocusable(false);
		sortOptions_colFocals.setEnabled(false);
		gbc.gridx = 2;
		gbc.gridy = 2;
		sortOptions.add(sortOptions_colFocals, gbc);
		

		
		sortOptions.setVisible(true);
		gbc.gridx = 0;
		gbc.gridy = 4;
		sortOptions_rows.setEnabled(false);
		sortOptions_table.setEnabled(false);
		sortOptions.setEnabled(false);
		sortOptions.setBackground(Color.LIGHT_GRAY);
		container.add(sortOptions, gbc);

		container.setVisible(true);
		add(subContainer);
		
		subContainer.add(container);
		//subContainer.setBorder(BorderFactory.createLineBorder(Color.BLACK)); 
		subContainer.setBackground(new Color(134, 184, 204));
		subContainer.setCursor(new Cursor(Cursor.HAND_CURSOR));
		subContainer.setVisible(true);

		mode1.doClick();
		
		stat.completed();
	}

	void populateTheList(JComboBox box) {
		
		String[] theList;
		SortTableType visopt = (SortTableType)sortOptions_table.getSelectedItem();
	
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
			
			if( sortOptions_rows.getSelectedItem().equals(SortOptions.FOCAL)) {				
				DefaultComboBoxModel modl = new DefaultComboBoxModel(theList);
				box.setModel(modl);
			}
			if( sortOptions_cols.getSelectedItem().equals(SortOptions.FOCAL)) {
				DefaultComboBoxModel modl = new DefaultComboBoxModel(theList);
				box.setModel(modl);
			}
			
		}
		else {
			sortOptions_rowFocals.setModel(new DefaultComboBoxModel(new String[] {"focal"}));
			sortOptions_colFocals.setModel(new DefaultComboBoxModel(new String[] {"focal"}));
		}
	}

	public void setSaveButtonState(boolean state) {
		
		
		if( state == true ) {
			
			save_visualisation.setLabel("disable save");
			save_visualisation.setForeground(new Color(161, 212, 144));
			
		} else if( state == false ) {
			
			save_visualisation.setLabel("enable save");
			save_visualisation.setForeground(new Color(212, 161, 144));
			
		}
		
	}
	
	public void actionPerformed( ActionEvent e) {
		
		if( e.getSource() == visualize) {
			
			visualize();
		}
		if( e.getSource() == save_visualisation) {
			
			
			if (save_visualisation.getLabel().equalsIgnoreCase("save visualisation")) {
				
				String filename;
				TableTypes tableType = visFrame.visApplet.getCurrentTableType();
				switch (tableType) {
				case KEYS:

					filename = "KEYS_";
					filename += visFrame.visApplet.getVismodeString();
					filename += "-";
					filename += keywordRelations.getFocal();

					visFrame.visApplet.saveVisualisation(true, filename);
					break;

				case OBJ_S:

					filename = "OBJECT-S_";
					filename += visFrame.visApplet.getVismodeString();
					filename += "_";
					filename += objectRelationsSimple.getFocal();

					visFrame.visApplet.saveVisualisation(true, filename);
					break;

				case SORTED:

					filename = "" + sortedTable.getTablesType();
					filename += "_";
					filename += visFrame.visApplet.getVismodeString();
					filename += "_";
					filename += objectRelationsSimple.getFocal();

					visFrame.visApplet.saveVisualisation(true, filename);
					break;

				default:

					break;
				}
			} else if( save_visualisation.getLabel().equalsIgnoreCase("enable save") ) {
				
				save_visualisation.setLabel("disable save");
				save_visualisation.setForeground(new Color(161, 212, 144));
				
			} else if( save_visualisation.getLabel().equalsIgnoreCase("disable save") ) {
				
				save_visualisation.setLabel("enable save");
				save_visualisation.setForeground(new Color(212, 161, 144));
				
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
		if( e.getSource() == sortOptions_table ) {

			populateTheList(null);
		}
		if( e.getSource() == sortOptions_go ) {
			
			doSort();
		}
		
//		if( e.getSource() == adcsort ) {
//			
//			sortRelationTableADC();
//			
//		}
		
		if( e.getSource() == visTable) {
			
		    visTable.getSelectedItem();
			if( visTable.getSelectedItem() == TableTypes.SORTED) {

				sortOptions_rows.setEnabled(true);
				sortOptions_cols.setEnabled(true);
				sortOptions_table.setEnabled(true);
				sortOptions_go.setEnabled(true);
				sortOptions.setEnabled(true);
				sortOptions.setBackground(Color.PINK);
				
			}
			else {
				
				sortOptions_rows.setEnabled(false);
				sortOptions_rows.setSelectedIndex(0);
				sortOptions_cols.setEnabled(false);
				sortOptions_cols.setSelectedIndex(0);
				sortOptions_table.setEnabled(false);
				sortOptions_colFocals.setEnabled(false);
				sortOptions_rowFocals.setEnabled(false);
				sortOptions.setEnabled(false);
				sortOptions_go.setEnabled(true);
				sortOptions.setBackground(Color.LIGHT_GRAY);
			}
			dprint("vistable changed");
		}
		
		
		if( e.getSource().equals(sortOptions_rows )) {
			
			if( sortOptions_rows.getSelectedItem() == SortOptions.FOCAL ) {
				sortOptions_rowFocals.setEnabled(true);
			} else {
				sortOptions_rowFocals.setEnabled(false);
			}
			populateTheList(sortOptions_rowFocals);
		}
		if( e.getSource().equals(sortOptions_cols )) {
			
			if( sortOptions_cols.getSelectedItem() == SortOptions.FOCAL ) {
				sortOptions_colFocals.setEnabled(true);
			} else {
				sortOptions_colFocals.setEnabled(false);
			}
			populateTheList(sortOptions_colFocals);
		}
		
		
		
		
		
		if( e.getSource() == mode1 ) {
			currentVisMode = VisModes.GRID_HARD;
			save_visualisation.setLabel("save visualisation");
			save_all_sorted.setEnabled(true);
		}
		if( e.getSource() == mode2 ) {
			currentVisMode = VisModes.GRID_SOFT;
			save_visualisation.setLabel("save visualisation");
			save_all_sorted.setEnabled(true);
		}
		if( e.getSource() == mode3 ) {
			currentVisMode = VisModes.CIRCULAR;
			save_visualisation.setLabel("save visualisation");
			save_all_sorted.setEnabled(true);
		}
		if( e.getSource() == mode4 ) {
			currentVisMode = VisModes.PATH;
			save_visualisation.setLabel("save visualisation");
			save_all_sorted.setEnabled(true);
		}
		if( e.getSource() == mode5 ) {
			currentVisMode = VisModes.NEBULAR;
			
			save_all_sorted.setEnabled(false);
			
			save_visualisation.setLabel("enable save");
			save_visualisation.setForeground(new Color(212, 161, 144));
			
		}

		
	}
	
	public void closeVisFrame() {
		visFrame.visApplet.destroy();
		visFrame.visApplet.dispose();
		visFrame.visApplet = null;
		visFrame.dispose();
		visFrame = null;		
		System.gc();
		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.gc();
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
			sortRelationTableByFocal(currentType, currentWord);
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
	
	private void doSort() {
		
		SortTableType type  	= (SortTableType)sortOptions_table.getSelectedItem();
		
		SortOptions opt_row 	= (SortOptions)sortOptions_rows.getSelectedItem();
		SortOptions opt_col 	= (SortOptions)sortOptions_cols.getSelectedItem();
		
		String focal_r			= sortOptions_rowFocals.getSelectedItem().toString(); 
		String focal_c			= sortOptions_colFocals.getSelectedItem().toString();	
		
		String[] rowIndex;
		String[] colIndex;
		
		
		RelationTable tableToSort;
		
		switch (type) {
		case KEYS:
			tableToSort = keywordRelations;
			break;
		case OBJ_S:
			tableToSort = objectRelationsSimple;
			break;
		case OBJ_M:
			tableToSort = objectRelationsMeta;
			break;
		default:
			tableToSort = null;
			break;
		}
		
		switch (opt_row) {
		case MOST_OCCURANCES:
			rowIndex = tableToSort.getSortedTermsByOccurences();
			break;
		case MOST_RELATED:
			rowIndex = tableToSort.getSortedTermsByTotalRelatednes();
			break;
		case FOCAL:
			rowIndex = tableToSort.getSortedTerms(focal_r);
			break;
		default:
			rowIndex = null;
			break;
		}
		
		switch (opt_col) {
		case MOST_OCCURANCES:
			colIndex = tableToSort.getSortedTermsByOccurences();
			break;
		case MOST_RELATED:
			colIndex = tableToSort.getSortedTermsByTotalRelatednes();
			break;
		case FOCAL:
			colIndex = tableToSort.getSortedTerms(focal_c);
			break;
		default:
			colIndex = null;
			break;
		}
		
		TableTypes tbltp = TableTypes.valueOf(type.toString());
		
		sortedTable = new RelationTable(this, tbltp, tableToSort, rowIndex, colIndex, focal_r, focal_c);
		
//		switch (type) {
//		
//		case KEYS:
//			
//			sortedTable = new RelationTable( this, TableTypes.KEYS, keywordRelations, keywordRelations.getSortedTerms(focal), focal );
//			break;
//			
//		case OBJ_S:
//			sortedTable = new RelationTable( this, TableTypes.OBJ_S, objectRelationsSimple, objectRelationsSimple.getSortedTerms(focal), focal );
//			break;
//			
//		case OBJ_M:
//			sortedTable = new RelationTable( this, TableTypes.OBJ_M, objectRelationsMeta, objectRelationsMeta.getSortedTerms(focal), focal );
//			break;
//			
//		default:
//			break;
//		}
		
		dprint("sorted Table populated");
		dprint("");
		
		
		
		
		
//		if( !focal.equalsIgnoreCase("none") ) {
//			
//			if( e.getSource() == sortOptions_go)    sortRelationTableByFocal(type, focal);
//			if( e.getSource() == sortOptions_path)  {
//				
//				switch (type) {
//				case KEYS:
//					keywordRelations.findPathForFocal(focal, 1000);
//					break;
//				case OBJ_S:
//					objectRelationsSimple.findPathForFocal(focal, 1000);
//				case SORTED:
//					sortedTable.findPathForFocal(focal, 1000);
//				default:
//					break;
//				}
//				
//			}
//		}
//		else {
//			JOptionPane.showMessageDialog(frame,
//				    "no focal point selected!",
//				    "error",
//				    JOptionPane.WARNING_MESSAGE);
//		}
		
	}

//	void sortRelationTableADC() {
//		
//		TableTypes type = (TableTypes)sortOptions_table.getSelectedItem();
//
//		
//		switch (type) {
//		case KEYS:
//			sortedTable = new RelationTable( this, TableTypes.KEYS, keywordRelations, keywordRelations.getSortedTermsByTotalRelatednes(), keywordRelations.getSortedTermsByTotalRelatednes(), "adc" );
////			sortedTable = new RelationTable( this, TableTypes.KEYS, keywordRelations, keywordRelations.getSortedTermsByOccurences(), "adc" );
//			break;
//		case OBJ_S:
//			sortedTable = new RelationTable( this, TableTypes.OBJ_S, objectRelationsSimple, objectRelationsSimple.getSortedTermsByTotalRelatednes(), "adc" );
//			break;
//		case OBJ_M:
//			sortedTable = new RelationTable( this, TableTypes.OBJ_M, objectRelationsMeta, objectRelationsMeta.getSortedTermsByTotalRelatednes(), "adc" );
//			break;
//		default:
//			break;
//		}
//		
//		//for(String a : sortedList) System.out.println(a);
//		dprint("sorted Table populated");
//		dprint("");
//		
//	}
	
	void sortRelationTableByFocal( TableTypes type, String focal) {

		switch (type) {
		case KEYS:
			sortedTable = new RelationTable( this, TableTypes.KEYS, keywordRelations, keywordRelations.getSortedTerms(focal), focal );
			break;
		case OBJ_S:
			sortedTable = new RelationTable( this, TableTypes.OBJ_S, objectRelationsSimple, objectRelationsSimple.getSortedTerms(focal), focal );
			break;
		case OBJ_M:
			sortedTable = new RelationTable( this, TableTypes.OBJ_M, objectRelationsMeta, objectRelationsMeta.getSortedTerms(focal), focal );
			break;
		default:
			break;
		}
		
		dprint("sorted Table populated");
		dprint("");

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
				elem = (String)elem.subSequence(1, elem.length()-1);  // Do this to get rid of the Anf�hrungszeichen
				exhibitsDataInt.put(elem, i++);
				dprint(exhibitsDataInt.get(elem) +" "+ elem);
			}
			exhibitsData.initDataInt(exhibitsDataInt);

			
			//now, fill the XML with the data from baruch...
			while (dataRow != null){
				
				XML row = new XML("ROW");
				dataArray = dataRow.split(",");
				
				for (String item:dataArray) { 
					if(item.length()>2) item = (String)item.subSequence(1, item.length()-1);  // Do this to get rid of the Anf�hrungszeichen
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
			
			boolean savebool;
			
			if( save_visualisation.getLabel().equals("disable save")) {
				savebool = true;
			} else {
				savebool = false;
			}
			
			TableTypes type = (TableTypes)visTable.getSelectedItem();

			visApplet.setVisualisationParams(currentVisMode, type, savebool);
			   
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

