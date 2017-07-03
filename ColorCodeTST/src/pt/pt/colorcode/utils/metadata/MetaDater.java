package pt.pt.colorcode.utils.metadata;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MetaDater {

	public static void saveMetadataToFile(File file, ArrayList<DataField> data) throws IOException {
		
		saveMetadataToFile(file, data.toArray(new DataField[data.size()]));
	}
	
	public static void saveMetadataToFile(File file, DataField[] data) throws IOException {

		if( !file.exists() ) {
			
			System.out.println("file doesn't exist. creating: " + file.getAbsolutePath());
			file.getParentFile().mkdirs(); 
			file.createNewFile();
		}

		BufferedWriter writer = new BufferedWriter( new FileWriter( file.getAbsolutePath()) );
		
		writer.write("########################################################################\n");
		writer.write("#                                                                      #\n");
		writer.write("#   Metadata for colorCode Project by Philipp Tögel and Monai Antunes  #\n");
		writer.write("#                                                                      #\n");
		writer.write("########################################################################\n\n\n\n");
		
		
		int maxSize = 0;
		for( DataField f : data) {
			if( f.getKey().length() > maxSize ) maxSize = f.getKey().length(); 
		}

		for( DataField f : data) {

			String s = "   ";
			
			
			int delta = maxSize - f.getKey().length();

			for (int i = 0; i < delta ; i++) {
				s += " ";
			}
			
			s += f.getKey() + "  :  " + f.getDataAsString();

			writer.write(s);
			writer.append("\n");
		}


		writer.close( );
	}

}
