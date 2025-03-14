import components.queue.Queue;
import components.sequence.Sequence;
import components.simplereader.SimpleReader;

public class Video_Title {

    /**
     * No argument constructor--private to prevent instantiation.
     */
    private Video_Title() {
    }

    private static final int VID_LINK = 0;
    private static final int VID_TITLE = 1;
    private static final int CHN_LINK = 2;
    private static final int CHN_NAME = 3;
    private static final int VIW_TIME = 4;

    private static final int UPPER_LIMIT = 60;

    private static final String FAIL_STRING = "";   // Fail to read

    private static final String DELETED = "Deleted_or_Disclosed";
    private static final String POST = "youtube.com/post/";
    private static final String IS_DELETED = "youtube.com/watch";

    private static String vid_title_filter(SimpleReader in) 
    {
        String result = "";
        char chr = in.read();
        while (chr != '<') 
        {
            result += chr;
            chr = in.read();
        }
        // In case when title has '<'
        String separator = "<" + in.read() + in.read() + in.read(); // '</a>'
        if (!separator.equals("</a>")) 
        {
            result += separator;
            result += vid_title_filter(in);
        }
        return result;
    }
    
    // action if watched video is deleted or disclosed
    private static void deleted_case(String result, SimpleReader in, 
                                     Sequence<Queue<String>> view_history)
    {
        view_history.entry(VID_TITLE).enqueue(DELETED);
        view_history.entry(CHN_LINK).enqueue(DELETED);
        view_history.entry(CHN_NAME).enqueue(DELETED);
        View_Time.extract_viewing_time(in, view_history.entry(VIW_TIME));
    }

    public static boolean extract_video_title (SimpleReader in, Sequence<Queue<String>> view_history) 
    {
        String vid_title = vid_title_filter(in);
        boolean end_of_stream = vid_title.equals(FAIL_STRING);
        if (end_of_stream) return false; // if everything read
        if (vid_title.contains(IS_DELETED)) 
        {
            deleted_case(vid_title, in, view_history);
            return false;
        }

        
        if (vid_title.length() >= UPPER_LIMIT) 
        {
            // maximum length limit = 60
            // upper limit can be adjusted
            vid_title = vid_title.substring(0, UPPER_LIMIT); 
        }
        vid_title = Viewhistory_extractor.data_adjustment(vid_title);
        view_history.entry(VID_TITLE).enqueue(vid_title);       
        return true;
    }
}
