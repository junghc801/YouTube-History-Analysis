import components.queue.Queue;
import components.queue.Queue1L;
import components.sequence.Sequence;
import components.sequence.Sequence1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

public class Channel_Name {

    /**
     * No argument constructor--private to prevent instantiation.
     */
    private Channel_Name() {
    }

    private static final String FAIL_STRING = "";   // Fail to read

    private static String channel_name_filter(SimpleReader in) 
    {
        String result = "";
        char chr = in.read();
        while (chr != '<') 
        {
            result += chr;
            chr = in.read();
        }
        // In case when name has '<'
        String separator = "<" + in.read() + in.read() + in.read(); // '</a>'
        if (!separator.equals("</a>")) 
        {
            result += separator;
            result += channel_name_filter(in);
        }
        return result;
    }
    
    public static boolean extract_channel_name (SimpleReader in, Queue<String> channel_name) 
    {
        String name = channel_name_filter(in);
        boolean end_of_stream = name.equals(FAIL_STRING);
        if (end_of_stream) return false; // if everything read
        name = Extractor_Revision.data_adjustment(name);
        channel_name.enqueue(name);
        return true;
    }
    



    
}
