
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

public final class Channels_Encoding {
    /**
     * No argument constructor--private to prevent instantiation.
     */
    private Channels_Encoding() {
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

        String input = "data/EXAMPLE.csv";
        /*
         * Encode youtube channel names and links and print out a csv file with replaced ones
         */
        
        
        /*
         * Close input and output streams
         */
        in.close();
        out.close();
    }
}