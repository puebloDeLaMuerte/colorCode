package colorcodetst;

import java.util.*;

import colorcodetst.ColorCodeTST.tables;


public class RelationTable {

	private boolean debug = false;
	
	private LinkedHashMap<String, LinkedHashMap<String, Integer>> table;
	private LinkedHashMap<Integer, String> index_col;
	private LinkedHashMap<Integer, String> index_row;
	private tables myType;
	private String focal;
	
	RelationTable( tables _Type, String[] terms ) {
		
		table = new LinkedHashMap<String, LinkedHashMap<String,Integer>>(terms.length,1);
		index_col = new LinkedHashMap<Integer, String>(terms.length, 1);
		index_row = new LinkedHashMap<Integer, String>(terms.length, 1);
		focal = "UNSORTED";
		myType = _Type;
		
		// fill the outer Map with new inner maps (+index)
		for(int i=0; i<terms.length; i++) {
			 table.put(terms[i], new LinkedHashMap<String, Integer>(terms.length, 1));
			 index_col.put(i, terms[i]);
			 index_row.put(i, terms[i]);
		}
		
		// fill the inner maps with key-value pairs
		for (LinkedHashMap<String,Integer> row : table.values()) {
			for(int i=0; i<terms.length; i++) {
				row.put(terms[i], 0);
			}
		}
		
	}
	
	RelationTable( tables _Type, RelationTable originTable, String[] order, String _focal) {
		
		table = new LinkedHashMap<String, LinkedHashMap<String,Integer>>(order.length,1);
		index_col = new LinkedHashMap<Integer, String>(order.length, 1);
		index_row = new LinkedHashMap<Integer, String>(order.length, 1);
		focal = _focal;
		
		if( _Type == tables.SORTED ){
			
			System.err.println("TYPE CANNOT BE 'SORTED'");
			
		} else {
			
			myType = _Type;

			// set up the rows-index from order[]
			for(int i = 0; i<order.length; i++ ) {
				index_row.put(i, order[i]);
			}

			// set up the index for columns from original table
			index_col = originTable.getColumnIndex();

			// put empty rows into table for later poputating them with values
			for(int i=0; i<order.length; i++) {
				table.put(order[i], new LinkedHashMap<String, Integer>(order.length, 1));
			}

			/* fill each row with the relation-values. 
			 * The iteration order being taken from index_col
			 */
			int c = 0;
			for (LinkedHashMap<String,Integer> row : table.values()) {
				for(int i=0; i<index_col.size(); i++) {
					row.put(index_col.get(i), originTable.getRelation(order[c], index_col.get(i)));
				}
				c++;
			}		
		}
	}

	
	void increaseRelation( String term1, String term2, int increment ) {
		
		dprint("gettingRelation for "+term1+" "+term2);
		
		if( !table.containsKey(term1) ) dprint( term1 +" NOT FOUND IN TABLE");
		else if ( !table.get(term1).containsKey(term2) ) dprint(term2 +" NOT FOUND IN TABLE"); 
		else {
			int current = table.get(term1).get(term2);
			table.get(term1).put(term2, current+increment);
		}
		
	}

	public tables getTablesType() {
		return myType;
	}
	
	int getRelation( String term1, String term2 ) {
		
		return table.get(term1).get(term2);
	}
	
	int getRelationByIndex( int row, int col ) {
		
		return getRelation( index_row.get(row), index_col.get(col) );
	}
	
	int getRowSize() {
		return index_row.size();
	}

	/* Auskommentiert um zu sehen wo es Ÿberhaut aufgerufen wird...
	 * 
	String[] getValuesArray() {
		return index.values().toArray(new String[getSize()]);
	}
	*/
	
	String[] getRowValuesArrayAlphabetically() {
		String[] a = index_row.values().toArray(new String[getRowSize()]);
		Arrays.sort(a);
		return a;
	}
	
	/*  Auskommentiert um zu sehen, wo es Ÿberhaupt jemals aufgerufen wird
	 *  
	 
	LinkedHashMap<String, Integer> getRelationMapFor(String focal ) {
		
		return table.get(focal);
	}
	*/
	
	void printTable() {
		
		System.out.println("...........................................................................");
		
		if( focal != null) dprint("This is a sorted relationTable. The focal is: "+focal);
		
		// Hunderter nach rechts antragen
		System.out.print("                ");
		for( int i=0; i< index_col.size();i++) {
			System.out.print((i/100)%10+" ");
		}
		System.out.println();
		
		// Zehner nach rechts antragen
		System.out.print("                ");
		for( int i=0; i< index_col.size();i++) {
			System.out.print((i/10)%10+" ");
		}
		System.out.println();
		
		// Einer nach rechs antragen
		System.out.print("                ");
		for( int i=0; i< index_col.size();i++) {
			System.out.print(i%10+" ");
		}
		System.out.println();
		
		// ...die Striche...
		System.out.print("                ");
		for( int i=0; i< index_col.size();i++) System.out.print("| ");
		System.out.println();
		
		// Die abgekŸrzten Begriffe nach rechts antragen - erste Zeile, jeder 2te Begriff
		System.out.print("                ");
		for( int i=0; i< index_col.size();i++) {
			if(i%2==0) {
				String s = index_col.get(i);
				if(s.length() >= 3) {
					s=s.substring(0, 3);
					System.out.print( s);
				}
				else {
					System.out.print( s);
					for(int p=0; p< 3-s.length(); p++) System.out.print(" ");
				}
			}
			else System.out.print(" ");
		}
		System.out.println();
		
		// Die andere HŠlfte der Begriffe nach rechts antraen
		System.out.print("                ");
		System.out.print(" ");
		for( int i=0; i< index_col.size();i++) {
			if(i%2==1) {
				String s = index_col.get(i);
				if(s.length() >= 3) {
					s=s.substring(0, 3);
					System.out.print( s);
				}
				else {
					System.out.print( s);
					for(int p=0; p< 3-s.length(); p++) System.out.print(" ");
				}
			}
			else System.out.print(" ");
		}
		System.out.println();
		
		// ...nochmal Striche...
		System.out.print("                ");
		for( int i=0; i< index_col.size();i++) System.out.print("| ");
		System.out.println();
		
		
		// Ab hier: Die Zeilen mit Index, Begriff und Daten:
		String str = "";
		int i=0;
		
		for (LinkedHashMap<String,Integer> row : table.values()) {
			
			// Index und Name der Zeile
			String rowName = index_row.get(i);
			if (rowName.length() > 8) rowName = rowName.substring(0, 8);
			System.out.print(String.format("%03d", i) +" "+rowName+" ");
			// ?? not shure if index row is really the right one in the following line of code
			for(int ii=0; ii< (8-index_row.get(i).length()); ii++) System.out.print("-");
			System.out.print("---");

			// Die Daten
			for(Integer value : row.values()) {
		    	if(value==0) str = ".";
		    	else if(value>=10) str = "¥";
		    	else str = value.toString();
		    	System.out.print(str+" ");
		    }
		    System.out.println();
		    i++;
		}
		
		System.out.println("...........................................................................");
	}

	
	public String[] getSortedIndices(String focal) {
		
		ArrayList<String> returnList = new ArrayList<String>();
		
		int max = getMaxValueFor(focal);
		
		if( max == 0 ) {
			dprint("THERE IS NO VALUES FOR THIS FOCAL !!");
		}
		
		for (int m = max; m >= 0; m--) {
			
			int tst;
			for (int i = 0; i < index_row.size(); i++) {

				tst = table.get(focal).get(index_col.get(i));
				if( tst == m ) returnList.add(index_col.get(i));
				
			}
		}
		return returnList.toArray(new String[returnList.size()]);
	}
	
	LinkedHashMap<Integer, String> getColumnIndex() {
		
		return index_col;
	}

	public String getTermByIndex(String _XY, int _idx) {
		
		String term;
		
		if( _XY.equalsIgnoreCase("column")) {
			
			term = index_col.get(_idx);
		}
		
		else if( _XY.equalsIgnoreCase("row")) {
			
			term = index_row.get(_idx);
		}
		
		else term = null;
		
		return term;
	}
	
	int getMaxValueFor(String _focal) {
		
		int mx = 0;
		int tst;
		for(int i=0; i<index_col.size(); i++) {
			
			if ( ! index_col.get(i).equalsIgnoreCase(_focal)) {
				tst = table.get(_focal).get(index_col.get(i));
				if (mx < tst)
					mx = tst;
			}
		}
				
		return mx;
	}

	int getMaxValueTotal() {

		int mx = 0;
		int tst;

		for( LinkedHashMap<String, Integer> row : table.values() ) {
			
			for(int i=0; i<index_col.size(); i++) {


				tst = row.get(index_col.get(i));
				if (mx < tst)
					mx = tst;

			}
		}

		return mx;
	}

	String getFocal() {
		
		if(focal != null && !focal.equalsIgnoreCase("")) return focal;
		else if( focal != null && focal.equalsIgnoreCase("") ) return "unsorted table";
		else return "focal: null";
	}
	
	private void dprint(Object _p) {
		if( debug ) System.err.println(_p);
	}
}















