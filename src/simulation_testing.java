import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import components.queue.Queue;
import components.queue.Queue1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * Extract youtube links, titles, channel links, channel names, and viewing time from data.
 * 
 * This version excludes post and music video histories. You can modify this feature in dataCleaning method.
 *
 * @author Haechan Jung
 */
// TODO: Appropriate year and time zone must be defined before execution.
public final class aa_hello {

    /**
     * No argument constructor--private to prevent instantiation.
     */
    private aa_hello() {
    }

    

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();


        String result = "result\n\ndfd\n\n\nn\nfg\n\n\n\nend\n";
        System.out.println(result);
        System.out.println("==================");
        result = result.replaceAll("\n", "");
        System.out.println(result);

        
        /*
         * Close input and output streams
         */
        in.close();
        out.close();
    }

}
