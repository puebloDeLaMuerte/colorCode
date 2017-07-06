package pt.pt.colorcodetst;

import processing.core.PGraphics;
import pt.pt.colorcode.utils.TableTypes;
import pt.pt.colorcode.utils.VisModes;


public interface VisInterface{


	public void sayHello();
	
	public boolean hasVisual();
	
	public String getVismodeString();
	
	public VisModes getVisMode();
		
	public TableTypes getCurrentTableType();
	
	public void visualize(RelationTable _table);
	
	public boolean isUpdateable();
	
	public void updateFrame();
	
	public boolean saveVisualisation(boolean _askForName, String _suggestedName);

	public PGraphics getVisualisationGraphics();
	
	public void setDisplayZoomValue(float _zoom);
	
	public void setDisplayOffset(int _xOffset, int _Yoffset);
	
	public boolean setType(TableTypes _type);
	
	public void drawFrame();
	
	public void destroy();
}
