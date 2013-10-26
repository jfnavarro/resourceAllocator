package resourceAllocator;
import java.io.IOException;
import java.util.ArrayList;

/** Reader is an abstract class to allow to have
 * different types of file parsers.
 * @author josefernandeznavarro
 *
 */
public abstract class Reader{
	public Reader() {	
	}
	public abstract <T extends Parsable> ArrayList<T> parseFile(String filename, Class<T> clazz) throws IOException;
}

