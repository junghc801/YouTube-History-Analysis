import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;

import javax.print.FlavorException;

import components.queue.Queue;
import components.queue.Queue1L;
import components.sequence.Sequence;
import components.sequence.Sequence1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * Copy of extractor file; created to revise(clean) code while doing the same task
 *
 * @author Haechan Jung
 */
public final class Extractor_Revision {

    /**
     * No argument constructor--private to prevent instantiation.
     */
    private Extractor_Revision() {
    }
    
    private static final int VID_LINK = 0;
    private static final int VID_TITLE = 1;
    private static final int CHN_LINK = 2;
    private static final int CHN_NAME = 3;
    private static final int VIW_TIME = 4;

    private static final String FAIL_STRING = "";

    private static final String DELETED = "Deleted_or_Disclosed";
    private static final String POST = "music.youtube.com/";
    private static final String MUSIC = "youtube.com/post/";
    private static final String IS_DELETED = "youtube.com/watch";
    private static final String HTML_DIV = "</div>";
    private static final DateFormat OLD_FORMAT = new SimpleDateFormat(
        "yyyy. MM. dd. a hh:mm:ss z", Locale.KOREA);
    private static final SimpleDateFormat NEW_FORMAT = new SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss");

    // ===================NEED TO BE MODIFIED IF NECESSARY===================
    private static int YEAR = 2024;
    private static final String DATA_YEAR1 = YEAR + ". ";
    private static final String DATA_YEAR2 = (YEAR + 1) + ". ";
    private static final String TIME_ZONE = "KST";

    private static String read_potential_time(SimpleReader in)
    {
        String time = "";
        while (!in.atEOS())
        {
            char chr = in.read();
            if (chr == '2')
            {
                time = ("2" + in.read() + in.read() + 
                            in.read() + in.read() + in.read()); // "2025. " expected
            }
            if (time.equals(DATA_YEAR1) || time.equals(DATA_YEAR2)) {
                // Extract 2024. 01......KST for example
                while (!time.contains(TIME_ZONE)) { time += in.read();}
                return time;
            }

        }
        return "";
        
    }
    private static String time_formatting(String time)
    {
        String result = FAIL_STRING;
        Date date;
        // Time formattting
        try 
        {
            date = OLD_FORMAT.parse(time);
            result = NEW_FORMAT.format(date);
        } 
        catch (ParseException e) 
        {
            e.printStackTrace();
        }
        return result;
    }

    private static String timeFilter(SimpleReader in) {
        final int six = 6;

        String result = FAIL_STRING;
        while (!in.atEOS()) 
        {
            String time = read_potential_time(in);
            if (!time.equals(FAIL_STRING))
            {
                /*
                 * Second validation: check if following is </div>;
                 * meaning truly found the time data 
                 */
                String div = "";
                for (int i = 0; i < six; i++) { div += in.read(); }
                if (div.equals(HTML_DIV)) 
                {
                    result = time_formatting(time);
                    break;
                }
            }   
        }
        return result;
    }

    private static String linkFilter(SimpleReader in) {
        String result = "";
        boolean found = false;
        while (!in.atEOS() && !found) {
            char chr = in.read();
            if (chr == 'h') {
                result = "h";
                for (int i = 0; i < 5 && !in.atEOS(); i++) { // 'href="'
                    result += in.read();
                }
                if (result.equals("href=\"")) {
                    result = "";
                    chr = in.read();
                    while (chr != '"') {
                        result += chr;
                        chr = in.read();
                    }
                    if (result.contains("youtube.com/")) {
                        found = true;
                        
                    }
                }
            }
        }
        if (found) {
            in.read(); // '>'
            return result;
        }
        return "";
    }

    private static String titleFilter(SimpleReader in) {
        String result = "";
        char chr = in.read();
        while (chr != '<') {
            result += chr;
            chr = in.read();
        }
        // In case when title has '<'
        String separator = "<" + in.read() + in.read() + in.read(); // '</a>'
        if (!separator.equals("</a>")) {
            result += separator;
            result += titleFilter(in);
        }
        return result;
    }
    private static boolean checkIf_good(String result){
        if (result.length() == 0) return false;
        if (result.contains(MUSIC) || 
            result.contains(POST) ) {   // Exclude music video history
            // timeFilter(in);
            // continue;
        }
        return false;
    }

    private static boolean extract_video_link (SimpleReader in, Queue<String> link){
        String vid_link = linkFilter(in);
        boolean end_of_stream = vid_link.equals(FAIL_STRING);
        if (end_of_stream) return false; // if read everything;
        link.enqueue(vid_link);          // if not yet
        return true;
    }

    private static boolean extract_video_title (SimpleReader in, Queue<String> title, 
                                            Sequence<Queue<String>> view_history){
        String vid_title = titleFilter(in);
        boolean end_of_stream = vid_title.equals(FAIL_STRING);
        if (end_of_stream) return false; // if read everything;
        if (vid_title.contains(IS_DELETED)) deleted_case(vid_title, in, view_history);
        return true;
    }

    private static boolean extract_channel_name (SimpleReader in){
        String name = FAIL_STRING;
        
        return true;
    }

    private static boolean extract_channel_link (SimpleReader in){
        String link;
        
        return true;
    }

    private static boolean extract_viewing_time (SimpleReader in, Queue<String> time_queue){
        String time = timeFilter(in);
        if (!time.equals(FAIL_STRING)) time_queue.enqueue(time);
        return !time.equals(FAIL_STRING);
    }
    
    // action if watched video is deleted or disclosed
    private static void deleted_case(String result, SimpleReader in, Sequence<Queue<String>> view_history){
        view_history.entry(VID_TITLE).enqueue(DELETED);
        view_history.entry(CHN_LINK).enqueue(DELETED);
        view_history.entry(CHN_NAME).enqueue(DELETED);
        extract_viewing_time(in, view_history.entry(VIW_TIME));
    }
    
    private static void dataCleaning(SimpleReader in, Sequence<Queue<String>> view_history) {

        
        // break if end of input stream
        while (!in.atEOS())
        {
            if (extract_video_link(in, view_history.entry(VID_LINK))) break;
            if (extract_video_title(in,view_history.entry(VID_TITLE), 
                                             view_history.entry(VID_LINK))) break;
            if (extract_channel_link(in, view_history.entry(CHN_LINK))) break;
            if (extract_channel_name(in, view_history.entry(CHN_NAME))) break;
            if (extract_viewing_time(in, view_history.entry(VIW_TIME))) break;
        }
        

        while (!in.atEOS()) {
            /*
             * Extract video link
             */
            String result = extract_video_link(in);
            if (!checkIf_good(result)) {
                reset_process(in);
                continue;
            }   
            link.enqueue(result);
            /*
             * Extract video title
             */
            result = titleFilter(in);
            boolean isDeleted = checkIF_deleted(result){
                if (result.contains("youtube.com/watch")) { // if deleted or undisclosed video 
                    title.enqueue("unknown");
                    channelLink.enqueue("unknown");
                    channelName.enqueue("unknown");
                    result = timeFilter(in);
                    if (result.length() < 1) {
                        break;
                    }
                    time.enqueue(result);
                    continue;
                }
            }
            
            data_
            result = result.replace(',', '_');  // To avoid wrong separation while making csv
            result = result.replace('/', '\\');
            result = result.replaceAll("\n",  "");
            title.enqueue(result);
            /*
             * Extract channel link
             */
            result = linkFilter(in);

            result = result.replace(',', '_');
            result = result.replace('/', '\\');
            result = result.replaceAll("\n",  "");
            channelLink.enqueue(result);
            /*
             * Extract channel name
             */
            result = titleFilter(in);

            result = result.replace(',', '_');
            result = result.replace('/', '\\');
            result = result.replaceAll("\n",  "");
            channelName.enqueue(result);
            // /*
            //  * Extract channel time
            //  */
            // result = timeFilter(in);
            // if (!result.equals(FAIL_STRING)) time.enqueue(result);
            
        }

    }

    private static void make_csv(SimpleWriter out, Sequence<Queue<String>> view_history) {
        assert view_history.entry(VID_LINK).length() == view_history.entry(VIW_TIME)
                .length() : "Violation of: two queues have correponding data";

        out.println("video_link,video_title,channel_link,channel_name,viewing_time"); // column names
        while (view_history.entry(VID_LINK).length() > 0) {
            out.print(view_history.entry(VID_LINK).dequeue() + ",");
            out.print(view_history.entry(VID_TITLE).dequeue() + ",");
            out.print(view_history.entry(CHN_LINK).dequeue() + ",");
            out.print(view_history.entry(CHN_NAME).dequeue() + ",");
            out.println(view_history.entry(VIW_TIME).dequeue());
        }

    }

    private static void assign_queues(Sequence<Queue<String>> view_history)
    {
        // 0: youtube video link
        // 1: youtube video title
        // 2: youtube channel name
        // 3: youtube channel name
        // 4: viewing time
        final int FIVE = 5;
        for (int i = 0; i < FIVE; i++)
        {
            view_history.add(i, new Queue1L<String>());
        }
        
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        /*
         * ====================NEED TO BE REPLACED====================
         */
        SimpleReader in = new SimpleReader1L("data/시청 기록.html"); 
        
        // initialization of data storage
        Sequence<Queue<String>> view_history = new Sequence1L();
        assign_queues(view_history);

        dataCleaning(in, view_history); // extract youtube data
        /*
         * ====================NEED TO BE REPLACED====================
         */
        String folder = "output"; 
        SimpleWriter out = new SimpleWriter1L(folder + "/youtube_view_history.csv");
        make_csv(out, view_history); // print data into text file
        /*
         * Close input and output streams
         */
        in.close();
        out.close();
    }

}
