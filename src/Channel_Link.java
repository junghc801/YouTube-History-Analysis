import components.queue.Queue;
import components.simplereader.SimpleReader;

public class Channel_Link {

    /**
     * No argument constructor--private to prevent instantiation.
     */
    private Channel_Link() {
    }

    private static final String FAIL_STRING = "";   // Fail to read

    private static final String CHANNEL = "youtube.com/channel/";

    private static final String LINK_NOTICE = "href=\"";
    private static final String IS_LINK = "youtube.com/";

    private static String channel_link_filter(SimpleReader in) 
    {
        String result = FAIL_STRING;
        while (!in.atEOS()) 
        {
            char chr = in.read();
            if (chr == 'h') 
            {
                result = "h";
                // 'href="'
                for (int i = 0; i < 5 && !in.atEOS(); i++) { result += in.read(); }
                if (result.equals(LINK_NOTICE)) 
                {
                    result = "";
                    chr = in.read();
                    while (chr != '"') 
                    {
                        result += chr;
                        chr = in.read();
                    }
                    if (result.contains(IS_LINK)) {
                        in.read(); // '>'
                        return result;
                    }
                }
            }
        }
        return FAIL_STRING;
    }

    private static boolean check_if_good_to_store(String result) 
    {
        if (result.equals(FAIL_STRING)) return false;
        // check if the link is channel related
        if (result.contains(CHANNEL)) return true;
        return false;
    }
    
    public static boolean extract_channel_link (SimpleReader in, Queue<String> link) 
    {
        String chnl_link = channel_link_filter(in);
        if (!check_if_good_to_store(chnl_link)) return false;
        boolean end_of_stream = chnl_link.equals(FAIL_STRING);
        if (end_of_stream) return false; // if everything read;
        // if not yet
        chnl_link = Extractor_Revision.data_adjustment(chnl_link);
        link.enqueue(chnl_link);          
        return true;
    }
    
}
