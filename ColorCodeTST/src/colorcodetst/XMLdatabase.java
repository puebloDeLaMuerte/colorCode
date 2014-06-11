package colorcodetst;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import processing.data.XML;

public class XMLdatabase extends XML {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<String, Integer> dataIntPairs;
	
	public XMLdatabase() {
		super();
		// TODO Auto-generated constructor stub
	}

	public XMLdatabase(File file) throws IOException,
			ParserConfigurationException, SAXException {
		super(file);
		// TODO Auto-generated constructor stub
	}

	public XMLdatabase(InputStream input) throws IOException,
			ParserConfigurationException, SAXException {
		super(input);
		// TODO Auto-generated constructor stub
	}

	public XMLdatabase(Reader reader) throws IOException,
			ParserConfigurationException, SAXException {
		super(reader);
		// TODO Auto-generated constructor stub
	}

	public XMLdatabase(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public XMLdatabase(File file, String options) throws IOException,
			ParserConfigurationException, SAXException {
		super(file, options);
		// TODO Auto-generated constructor stub
	}

	public XMLdatabase(InputStream input, String options) throws IOException,
			ParserConfigurationException, SAXException {
		super(input, options);
		// TODO Auto-generated constructor stub
	}

	public XMLdatabase(Reader arg0, String arg1) throws IOException,
			ParserConfigurationException, SAXException {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public XMLdatabase(XML parent, Node node) {
		super(parent, node);
		// TODO Auto-generated constructor stub
	}

	public void initDataInt(HashMap<String, Integer> _map){
		dataIntPairs = _map;
	}
	
 	public String[] hasConnections(String _col, String _term, String _return, int depth) {
		
		ArrayList<String> connections = new ArrayList<String>();
		int columnInt = -1; 
		try{
			columnInt = dataIntPairs.get(_col);
		} catch(Exception e) {
			System.err.println("the XMLdatabase does not contain a column named \""+_col+"\"");
			return null;
		}
		
		for(XML row: this.getChildren()) {
			if( row.getChild(columnInt).getContent().equals(_term) ){
				connections.add(row.getChild(dataIntPairs.get(_return)).getContent());
			}
		}
		
		
		String[] ret = new String[1];
		System.err.println("REESULT: "+connections);
		return connections.toArray(ret);
	}
	
}
