package colorcodetst;

import processing.core.PGraphics;
import MyUtils.TableTypes;
import MyUtils.VisModes;


public interface VisInterface{


	public void sayHello();
	
	public boolean hasVisual();
	
	public String getVismodeString();
	
	public VisModes getVisMode();
		
	public TableTypes getCurrentTableType();
	
	public void visualize(RelationTable _table);
	
	public boolean saveVisualisation(boolean _askForName, String _suggestedName);

	public PGraphics getVisualisationGraphics();
}
