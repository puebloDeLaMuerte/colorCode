package pt.pt.colorcode.utils.metadata;

public class DataField {
	
	private DataFieldType 	type;
	private String			key;
	private String			stringData;
	private float			floatData;
	private int				intData;
	
	public DataField(String key, String value) {
		
		this.type = DataFieldType.STRING;
		this.key = key;
		this.stringData = value;
	}

	public DataField(String key, int value) {

		this.type = DataFieldType.INT;
		this.key = key;
		this.intData = value;
	}

	public DataField(String key, float value) {

		this.type = DataFieldType.FLOAT;
		this.key = key;
		this.floatData = value;
	}

	public String getDataAsString() {
		
		switch (type) {
		
		case STRING: return stringData;
			
		case FLOAT: return ""+floatData;
			
		case INT: return ""+intData;

		default: return null;
		}		
	}
	
	public String getKey() {
		return key;
	}

	public String getStringData() {
		return stringData;
	}

	public float getFloatData() {
		return floatData;
	}

	public int getIntData() {
		return intData;
	}
	
	
}
