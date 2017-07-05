package pt.pt.colorcode.utils.quicksort;

public class SortElement {
	
	
	private float value;
	private Object name;
	
	public SortElement(String name, float value) {
		this.name = name;
		this.value = value;
	}
	
	public SortElement(Object obj, float value) {
		this.name = obj;
		this.value = value;
	}
	
	public float getValue() {
		return value;
	}
	
	public String getName() {
		
		if( name.getClass() == String.class ) {
			return name.toString();
		} else {
			return null;
		}
	}
	
	public Object getObject() {
		return name;
	}
}