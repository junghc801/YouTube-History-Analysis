import components.queue.Queue;
import components.simplereader.SimpleReader;

public class Video_Link {

    /**
     * No argument constructor--private to prevent instantiation.
     */
    private Video_Link() {
    }

    private static final String FAIL_STRING = "";   // Fail to read

    private static final String MUSIC = "music.youtube.com/";
    private static final String POST = "youtube.com/post/";

    private static final String LINK_NOTICE = "href=\"";
    private static final String IS_LINK = "youtube.com/";

    private static String video_link_filter(SimpleReader in) 
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
        // Exclude music video history
        // if (result.contains(MUSIC)) return false;
        // annotate below if want to store viewing post history
        // if (result.contains(POST)) return false; 
        return true;
    }
    
    public static boolean extract_video_link (SimpleReader in, Queue<String> link) 
    {
        String vid_link = video_link_filter(in);
        if (!check_if_good_to_store(vid_link)) return false;
        boolean end_of_stream = vid_link.equals(FAIL_STRING);
        if (end_of_stream) return false; // if everything read;
        // if not yet
        vid_link = Viewhistory_extractor.data_adjustment(vid_link);
        link.enqueue(vid_link);          
        return true;
    }
}
