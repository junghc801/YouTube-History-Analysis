import components.queue.Queue;
import components.sequence.Sequence;

public class Debugging {

    /**
     * No argument constructor--private to prevent instantiation.
     */
    private Debugging() {
    }

    private static final int VID_LINK = 0;
    private static final int VID_TITLE = 1;
    private static final int CHN_LINK = 2;
    private static final int CHN_NAME = 3;
    private static final int VIW_TIME = 4;

    public static boolean check_length(Sequence<Queue<String>> view_history) 
    {
        boolean result = true;
        int vid_link_len = view_history.entry(VID_LINK).length();
        int vid_title_len = view_history.entry(VID_TITLE).length();
        int chn_link_len = view_history.entry(CHN_LINK).length();
        int chn_name_len = view_history.entry(CHN_NAME).length();
        int viw_time_len = view_history.entry(VIW_TIME).length();
        if (vid_link_len == vid_title_len &&
            vid_title_len == chn_link_len &&
            chn_link_len == chn_name_len &&
            chn_name_len == viw_time_len)
        {
            result = true;   
        }
        else
        {
            System.out.println("(DEBUG) check_length result: ==============================================================");
            System.out.println("(DEBUG) Video link length: " + vid_link_len);
            System.out.println("(DEBUG) Video title length: " + vid_title_len);
            System.out.println("(DEBUG) Channel link length: " + chn_link_len);
            System.out.println("(DEBUG) Channel name length" + chn_name_len);
            System.out.println("(DEBUG) Viewing time length: " + viw_time_len);
            result = false;
        }
        return result;
    }
    
}
