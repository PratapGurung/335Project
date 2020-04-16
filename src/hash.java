import java.util.Enumeration;
import java.util.Hashtable;

public class hash {
  public static void main (String args[]) throws Exception {
    // Start with ten, expand by ten when limit reached
    Hashtable hash = new Hashtable(10,10);

    for (int i = 0; i <= 100; i++)
    {
	Integer integer = new Integer ( i );
	hash.put( integer, "Number : " + i);
    }

    // Get value out again
    System.out.println (hash.get(new Integer(5)));
    // Get value out again
    System.out.println (hash.get(new Integer(21)));

    System.in.read();

    // Get all values
    for (Enumeration<Integer> e = hash.keys(); e.hasMoreElements();)
    {
	System.out.println (hash.get(e.nextElement()));
    }
 }
}
