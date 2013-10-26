package resourceAllocator;
import java.io.IOException;

/** Parsable is an interface for the method parse
 * that will allow to have a generic file Parser, if only if,
 * the objects that the file is going to be parsed to implements
 * this interface.
 * @author josefernandeznavarro
 *
 */
public interface Parsable {
	public void parse(String line) throws IOException;
	//TODO setId should be part of other interface
	public void setId(String _month_id);
}
