import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;

import components.queue.Queue;
import components.simplereader.SimpleReader;

public class View_Time {
    
    /**
     * No argument constructor--private to prevent instantiation.
     */
    private View_Time() {
    }

    private static final String FAIL_STRING = "";   // Fail to read

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
    

    // extract potential time data: "2025.0 ... KST"
    private static String read_potential_time(SimpleReader in)
    {
        String time = FAIL_STRING;
        while (!in.atEOS())
        {
            char chr = in.read();
            if (chr == '2')
            {
                time = ("2" + in.read() + in.read() + 
                            in.read() + in.read() + in.read()); // "2025. " expected
            }
            if (time.equals(DATA_YEAR1) || time.equals(DATA_YEAR2)) 
            {    
                while (!time.contains(TIME_ZONE)) { time += in.read();}
                return time;
            }
        }
        return FAIL_STRING;
        
    }
    // modify time format
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

    // filter time data
    private static String timeFilter(SimpleReader in) 
    {
        final int six = 6;

        String result = FAIL_STRING;
        while (!in.atEOS()) 
        {
            String time = read_potential_time(in);
            if (!time.equals(FAIL_STRING))
            {
                /* Second validation: check if following is </div>;
                 *             meaning truly found the time data */
                String div = "";
                for (int i = 0; i < six; i++) { div += in.read(); }
                if (div.equals(HTML_DIV)) 
                {
                    result = time_formatting(time);
                    return result;
                }
            }   
        }
        return FAIL_STRING;
    }

    public static boolean extract_viewing_time (SimpleReader in, Queue<String> time_queue)
    {
        String time = timeFilter(in);
        if (!time.equals(FAIL_STRING)) time_queue.enqueue(time);
        return !time.equals(FAIL_STRING);
    }
}
