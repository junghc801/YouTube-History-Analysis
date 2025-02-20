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

    private static final String DELETED = "Deleted_or_Disclosed";
    private static final String POST = "music.youtube.com/";
    private static final String MUSIC = "youtube.com/post/";
    private static final String IS_DELETED = "youtube.com/watch";
    private static final DateFormat OLD_FORMAT = new SimpleDateFormat(
        "yyyy. MM. dd. a hh:mm:ss z", Locale.KOREA);
    private static final SimpleDateFormat NEW_FORMAT = new SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss");

    private static int YEAR = 2024;
    private static final String DATA_YEAR1 = YEAR + ". ";
    private static final String DATA_YEAR2 = (YEAR + 1) + ". ";
    private static final String TIME_ZONE = "KST";

    private static String timeFilter(SimpleReader in) {
        boolean done = false;
        
        String result = "";
        while (!in.atEOS() && !done) {
            String time;
            char chr = in.read();
            if (chr == '2') {
                time = "2" + in.read() + in.read() + in.read() + in.read() + in.read();
                /*
                 * ===================NEED TO BE MODIFIED IF NECESSARY===================
                 */
                if (time.equals(DATA_YEAR1) || time.equals(DATA_YEAR2)) { 
                    while (!time.contains(TIME_ZONE)) {
                        time += in.read();
                    }
                    /*
                     * Check if following is </div>
                     */
                    String div = "";
                    final int six = 6;
                    for (int i = 0; i < six; i++) {
                        div += in.read();
                    }
                    if (div.equals("</div>")) {
                        Date date;
                        try {
                            date = OLD_FORMAT.parse(time);
                            result = NEW_FORMAT.format(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        done = true;
                    }

                }
            }
        }
        if (done) {
            return result;
        }
        return "";
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

    private static String extract_video_link (SimpleReader in){
        String link;
        link = linkFilter(in);
        if (checkIf_good(link)) return null;
        return link;
    }

    private static String extract_video_title (SimpleReader in){
        String title;
        
        return title;
    }

    private static String extract_channel_name (SimpleReader in){
        String name;
        
        return name;
    }

    private static String extract_channel_link (SimpleReader in){
        String link;
        
        return link;
    }

    private static String extract_viewing_time (SimpleReader in){
        String time;
        
        return time;
    }

    private static void reset_process(SimpleReader in)
    {
        timeFilter(in);
    }

    private static boolean checkIF_deleted(String result, SimpleReader in){
        if (result.contains(IS_DELETED) { // if deleted or undisclosed video 
            title.enqueue(DELETED);
            channelLink.enqueue(DELETED);
            channelName.enqueue(DELETED);
            result = timeFilter(in);
            if (result.length() < 1) {
                break;
            }
            time.enqueue(result);
            continue;
        }
    }
    
    private static void dataCleaning(SimpleReader in, Queue<String> link,
            Queue<String> title, Queue<String> channelLink,
            Queue<String> channelName, Queue<String> time) {

        
        
        result = extract_video_title(in);
        result = extract_channel_name(in);
        result = extract_channel_link(in);
        result = extract_viewing_time(in);

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
            /*
             * Extract channel time
             */
            result = timeFilter(in);

            time.enqueue(result);
        }

    }

    private static void make_csv(SimpleWriter out, Queue<String> link,
            Queue<String> title, Queue<String> channelLink,
            Queue<String> channelName, Queue<String> time) {
        assert link.length() == time
                .length() : "Violation of: two queues have correponding data";

        out.println("video_link,video_title,channel_link,channel_name,viewing_time"); // column names
        while (link.length() > 0) {
            out.print(link.dequeue() + ",");
            out.print(title.dequeue() + ",");
            out.print(channelLink.dequeue() + ",");
            out.print(channelName.dequeue() + ",");
            out.println(time.dequeue());
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
        Queue<String> link = new Queue1L<>();           // youtube video link
        Queue<String> title = new Queue1L<>();          // youtube video title
        Queue<String> channelLink = new Queue1L<>();    // youtube channel name
        Queue<String> channelName = new Queue1L<>();    // youtube channel name
        Queue<String> time = new Queue1L<>();           // viewing time

        dataCleaning(in, link, title, channelLink, channelName, time); // extract youtube data
        /*
         * ====================NEED TO BE REPLACED====================
         */
        String folder = "output"; 
        SimpleWriter out = new SimpleWriter1L(folder + "/youtube_view_history.csv");
        make_csv(out, link, title, channelLink, channelName, time); // print data into text file
        /*
         * Close input and output streams
         */
        in.close();
        out.close();
    }

}
