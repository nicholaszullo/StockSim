import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Use this logger to save session data and to find outputs more easily by catagory.
 *  By using a logging class, we can move the data to wherever we need just by 
 *  using a different output stream
 */
public class Logger {
	public final int INFO = 0;
	public final int FATAL = 1;
	public final int DEBUG = 2;
	public final int ERROR = 3;

	/**	If an out is not specified, use the same one the program is already using
	 * 
	 */
	public Logger(){
		this(System.out);
	}

	/**	Change the output to be the stream specified, usually a file 
	 *  new PrintStream(new FileOutputStream("file"))
	 * @param out The output to now use for logging
	 */
	public Logger(PrintStream out){
		System.setOut(out);
		System.out.println("\nNew session on " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
	}

	/** Display a message with the specified classification
	 * 
	 * @param level .INFO, .FATAL, .DEBUG - The classification of the message
	 * @param s The message to output
	 */
	public void out(int level, String s){
		switch (level) {
			case 0: {
				System.out.println("Info: " + s);
				break;
			}
			case 1: {
				System.out.println("------\nFATAL ERROR: " + s + "\n------");
				break;
			}
			case 2:{
				System.out.println("Debug info: " + s);
				break;
			}
			case 3: {
				System.out.println("ERROR: " + s);
			}
			default: {
				System.out.println("Unspecified level: " + s);
				break;
			}
		}
	}

}
