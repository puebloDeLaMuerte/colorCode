package colorcodetst;

import java.util.*;

import MyUtils.StatusGui;
import MyUtils.TableTypes;


public class RelationTable {

	private boolean debug = false;
	
	private LinkedHashMap<String, LinkedHashMap<String, Integer>> table;
	private LinkedHashMap<Integer, String> index_col;
	private LinkedHashMap<Integer, String> index_row;
	private String[] pathForFocal_S;
	private int[] pathForFocal_I;
	private TableTypes myType;
	private String focal;
	private ColorCodeTST parent;
	
	RelationTable( ColorCodeTST _parent, TableTypes _Type, String[] terms ) {
		
		parent = _parent;
		
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
	
	RelationTable( ColorCodeTST _parent, TableTypes _Type, RelationTable originTable, String[] order, String _focal) {
		
		parent = _parent;
		
		table = new LinkedHashMap<String, LinkedHashMap<String,Integer>>(order.length,1);
		index_col = new LinkedHashMap<Integer, String>(order.length, 1);
		index_row = new LinkedHashMap<Integer, String>(order.length, 1);
		focal = _focal;
		
		if( _Type == TableTypes.SORTED ){
			
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

	
	public void increaseRelation( String term1, String term2, int increment ) {
		
		dprint("gettingRelation for "+term1+" "+term2);
		
		if( !table.containsKey(term1) ) dprint( term1 +" NOT FOUND IN TABLE");
		else if ( !table.get(term1).containsKey(term2) ) dprint(term2 +" NOT FOUND IN TABLE"); 
		else {
			int current = table.get(term1).get(term2);
			table.get(term1).put(term2, current+increment);
		}
		
	}

	public TableTypes getTablesType() {
		return myType;
	}
	
	public String getTablesTypeAsString() {
		return myType.toString();
	}
	
	public int getRelation( String term1, String term2 ) {
		
		return table.get(term1).get(term2);
	}
	
	public int getRelationByIndex( int row, int col ) {
		
		return getRelation( index_row.get(row), index_col.get(col) );
	}
	
	public int getRowIndexForString( String _in ) {
		
		int count = 0;
		for(String s : index_row.values()) if( s.equalsIgnoreCase(_in)) break;	
		return count;
	}
	
	public int getColIndexForString( String _in ) {

		int count = 0;
		for(String s : index_col.values()) if( s.equalsIgnoreCase(_in)) break;	
		return count;
	}
	
	public LinkedHashMap<String, Integer> getRowByTerm(String _term) {
		
		return table.get(_term);
	}
	
	public int getRowSize() {
		return index_row.size();
	}

	/* Auskommentiert 
	 * 
	String[] getValuesArray() {
		return index.values().toArray(new String[getSize()]);
	}
	*/
	
	public String[] getRowValuesArrayAlphabetically() {
		String[] a = index_row.values().toArray(new String[getRowSize()]);
		Arrays.sort(a);
		return a;
	}
	
	/*  Auskommentiert 
	 *  
	 
	LinkedHashMap<String, Integer> getRelationMapFor(String focal ) {
		
		return table.get(focal);
	}
	*/
	
	public void findPathForFocal( String _focal, int maximumPathDepth) {
		
		//TODO: handle the case where nothing is found (indx still is -1)
		
		dprint("");
		dprint("");
		dprint("");
		dprint("#####################################");
		dprint("finding path for" + _focal);
		dprint("#####################################");
		
		parent.stat = new StatusGui();
		parent.stat.update(0, "finding path for" + _focal);
		
		
		ArrayList<String> path = new ArrayList<String>();
		int []topRelatedIdcs;
		int nextTermIdx = -1;
		
		String currentFocal = _focal;
		path.add(_focal);

		while( path.size() < maximumPathDepth){
			
			//topRelatedIdcs = getMostRelatedIndices(currentFocal, path);
			topRelatedIdcs = getLeastRelatedIndices(currentFocal, path);
			
			if( topRelatedIdcs.length > 0 ) {

				if( topRelatedIdcs.length == 1) { 		// if there's a single top related term, set it as NEXT
					nextTermIdx = topRelatedIdcs[0];
				}
				if( topRelatedIdcs.length > 1) {		// if there's more equally related ones, see who's most relatet in total and set this as NEXT
					int currentTopIdx = -1;					// use the latest and highest, no sensitive choice if multiple most related values
					int currentTopVal = 0;
					for( int i : topRelatedIdcs) {
						int x = getTotalRelatednes( index_col.get(i));
						if( x > currentTopVal) {
							currentTopIdx = i;
							currentTopVal = x;
						}
					}
					nextTermIdx  = currentTopIdx;
				}
				path.add(index_col.get(nextTermIdx));
			} else {
				
				break;  // if no topRelated is found (does it happen?
				
			}
			currentFocal = index_col.get(nextTermIdx);
			
			if( index_col.get(nextTermIdx).equalsIgnoreCase(_focal)){
				
				dprint("path reached its own focal after " + path.size() +" steps.");
				break;
			}

			parent.stat.update2("current path size: "+path.size());
		}
		parent.stat.completed();

		focal = _focal;
		pathForFocal_S = path.toArray(new String[path.size()]);
		
		dprint("path length: "+path.size());
		for( String s : path) {
			dprint(s);
		}
		parent.stat.end();
	}
	
	public void printTable() {
		
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
		
		// Die abgek�rzten Begriffe nach rechts antragen - erste Zeile, jeder 2te Begriff
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
		
		// Die andere H�lfte der Begriffe nach rechts antraen
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
		    	else if(value>=10) str = "+";
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
	
	public LinkedHashMap<Integer, String> getColumnIndex() {
		
		return index_col;
	}

	public LinkedHashMap<Integer, String> getRowIndex() {
		return index_row;
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
	
	private int getTotalRelatednes( String _term ) {
		
		LinkedHashMap<String, Integer> row = table.get(_term);
		int count = 0;
		
		for( Integer x : row.values() ) {
			count += x;
		}
		
		return count;
	}
	
	private int[] getMostRelatedIndices(String _focal, ArrayList<String> _lastFocals) {
		
		int[] returnArray;
		ArrayList<Integer> values = new ArrayList<Integer>();

		LinkedHashMap<String, Integer> row = table.get(_focal);
		int mx = 0;
		int tst;
		
		// finding top values
		for(int i=0; i<index_col.size(); i++) {

			boolean isAlreadyInPath = false;
			for(String s : _lastFocals) {
				
				if( s.equalsIgnoreCase(index_col.get(i))) isAlreadyInPath = true;
			}
			if( ! index_row.get(i).equalsIgnoreCase(_focal) && !isAlreadyInPath) {  // if you're not yourself or already in the list

				tst = row.get(index_col.get(i));
				if( tst > mx) {
					mx = tst;
					values = new ArrayList<Integer>();
					values.add(i);
				} else
				if( tst == mx ) {
					values.add(i);
				}
			}
		}

		// convert ArrayList into Array
		returnArray = new int[values.size()];
		for(int i = 0; i < values.size(); i++) {
			returnArray[i]  = values.get(i); 
		}
		return returnArray;
	}
	
	private int[] getLeastRelatedIndices(String _focal, ArrayList<String> _lastFocals) {
		
		int[] returnArray;
		ArrayList<Integer> values = new ArrayList<Integer>();

		LinkedHashMap<String, Integer> row = table.get(_focal);
		int min = getMaxValueFor(_focal);
		int tst;
		
		// finding top values
		for(int i=0; i<index_col.size(); i++) {

			boolean isAlreadyInPath = false;
			for(String s : _lastFocals) {
				
				if( s.equalsIgnoreCase(index_col.get(i))) isAlreadyInPath = true;
			}
			if( ! index_row.get(i).equalsIgnoreCase(_focal) && !isAlreadyInPath) {  // if you're not yourself or already in the list

				tst = row.get(index_col.get(i));
				if( tst < min) {
					min = tst;
					values = new ArrayList<Integer>();
					values.add(i);
				} else
				if( tst == min ) {
					values.add(i);
				}
			}
		}

		// convert ArrayList into Array
		returnArray = new int[values.size()];
		for(int i = 0; i < values.size(); i++) {
			returnArray[i]  = values.get(i); 
		}
		return returnArray;
	}
	
	public int getMaxValueFor(String _focal) {
		
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

	public int getMaxValueTotal() {

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
	
	public String getFocal() {
		
		if(focal != null && !focal.equalsIgnoreCase("")) return focal;
		else if( focal != null && focal.equalsIgnoreCase("") ) return "unsorted table";
		else return "focal: null";
	}
	
	public boolean hasPath() {
		
		if( pathForFocal_S != null) return true;
		else return false;
	}
	
 	public String[] getPath() {
		return pathForFocal_S;
	}
	
	private void dprint(Object _p) {
		if( debug ) System.err.println(_p);
	}
}















