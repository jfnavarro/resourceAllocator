package resourceAllocator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.lang.Class;

/** Sub class of Reader that implements the parse file method
 * for a "weird" text format. It is generic so it can be used with
 * different objects as long as the object implement the Interface Parsable.
 * @author josefernandeznavarro
 *
 */
public class ReaderWeirdFormat extends Reader {
	
	public <T extends Parsable> ArrayList<T> parseFile(String filename, Class<T> clazz) throws IOException {	
		ArrayList<T> costumers = new ArrayList<T>();
		FileReader input = new FileReader(filename);
		BufferedReader bufRead = new BufferedReader(input);
		String line = null;
		String month_id = null;
		while ( (line = bufRead.readLine()) != null) {   
			if(line.length() > 0) {
				if (!line.contains(",")) {
					month_id = line.trim();
				}else {
					T new_costumer = null;
					try {
						new_costumer = clazz.newInstance();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					new_costumer.parse(line);
					new_costumer.setId(month_id);
					costumers.add(new_costumer);
				}
			}
		}
		input.close();
		return costumers;
	}
}