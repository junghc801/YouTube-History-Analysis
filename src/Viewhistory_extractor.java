import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

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
public final class Viewhistory_extractor {

    /**
     * No argument constructor--private to prevent instantiation.
     */
    private Viewhistory_extractor() {
    }
    /*
     * Debugging purpose only
     */
    private static final boolean DEBUG = false; 

    /*
     * index for sequence
     */
    private static final int VID_LINK = 0;
    private static final int VID_TITLE = 1;
    private static final int CHN_LINK = 2;
    private static final int CHN_NAME = 3;
    private static final int VIW_TIME = 4;

    public static String data_adjustment(String original)
    {
        String corrected = original;

        
        String target = "SPECIFIC_TITLE";
        if (DEBUG) // =====================================
        {
            if (corrected.contains(target))
            {
                System.out.println("(DEBUG) title before correction: ");
                System.out.println(corrected + "===========\n");
            } 
        }
        // To avoid wrong separation while making csv
        corrected = corrected.replace(System.lineSeparator(),  " ");
        corrected = corrected.replace("\n", " ");
        corrected = corrected.replace("\r", " ");
        corrected = corrected.replace("\t", " ");
        corrected = corrected.replace(',', '_');  
        corrected = corrected.replace('/', '\\');
        
        if (DEBUG) // =====================================
        {
            if (corrected.contains(target))
            {   
                System.out.println("(DEBUG) title after correction: ======================");
                System.out.println(corrected);
            } 
        }
        return corrected;
    }

    private static void extract_view_history(SimpleReader in, Sequence<Queue<String>> view_history) 
    {
        // break if end of input stream
        while (!in.atEOS())
        {
            if (DEBUG) // ==========================================================================DEBUG
            {
                if (!Debugging.check_length(view_history))
                {
                    break;
                }
            }

            if (!Video_Link.extract_video_link(in, view_history.entry(VID_LINK)))     continue;
            if (!Video_Title.extract_video_title(in, view_history))                   continue;
            if (!Channel_Link.extract_channel_link(in, view_history.entry(CHN_LINK))) continue;
            if (!Channel_Name.extract_channel_name(in, view_history.entry(CHN_NAME))) continue;
            if (!View_Time.extract_viewing_time(in, view_history.entry(VIW_TIME)))    continue;
        }
    }

    private static void make_csv(SimpleWriter out, Sequence<Queue<String>> view_history) 
    {
        assert view_history.entry(VID_LINK).length() == view_history.entry(VIW_TIME).length() : 
                "Violation of: two queues have correponding data";

        // column names
        out.println("video_link,video_title,channel_link,channel_name,viewing_time");
        
        if (DEBUG) // ====================================================================DEBUG
        {
            System.out.println("(DEBUG) Video_Link: " + view_history.entry(VID_LINK).length());
            System.out.println("(DEBUG) Video_Title: " + view_history.entry(VID_TITLE).length());
            System.out.println("(DEBUG) Channel_link: " + view_history.entry(CHN_LINK).length());
            System.out.println("(DEBUG) Channel_Name: " + view_history.entry(CHN_NAME).length());
            System.out.println("(DEBUG) Viewing_Time: " + view_history.entry(VIW_TIME).length());
        }
        
        while (view_history.entry(VID_LINK).length() > 0) 
        {
            out.print(view_history.entry(VID_LINK).dequeue() + ",");
            out.print(view_history.entry(VID_TITLE).dequeue() + ",");
            out.print(view_history.entry(CHN_LINK).dequeue() + ",");
            out.print(view_history.entry(CHN_NAME).dequeue() + ",");
            out.println(view_history.entry(VIW_TIME).dequeue());
        }

        if (DEBUG) System.out.println("(DEBUG) FINISHED");

    }

    private static void init_queues(Sequence<Queue<String>> view_history)
    {
        /*
         * 0: youtube video link
         * 1: youtube video title
         * 2: youtube channel name
         * 3: youtube channel name
         * 4: viewing time
         */
        final int FIVE = 5;
        for (int i = 0; i < FIVE; i++)
        {
            view_history.add(i, new Queue1L<String>());
        }   
    }

    private static String read_input()
    {
        Scanner scanner = new Scanner(System.in);
        String filePath = "";
        try {
            filePath = scanner.nextLine();
            Path path = Paths.get(filePath);

            // Check if file exists
            if (!Files.exists(path)) {
                throw new NoSuchFileException("File not found: " + filePath);
            }

            // Read file content or perform other operations
            String content = new String(Files.readAllBytes(path));
            System.out.println("File content: " + content);

        } catch (NoSuchFileException e) {
            System.err.println("Error: File not found. " + e.getMessage());
            System.exit(1);
        } catch (InvalidPathException e) {
            System.err.println("Error: Invalid file path. " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Error: An I/O error occurred. " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("An unknown error occurred. " + e.getMessage());
            System.exit(1);
        } finally {
            scanner.close();
        }
        return filePath;
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) 
    {
        System.out.print("Input file directory(where the file locates): ");
        String input_dir = read_input();

        System.out.print("Output file directory(where csv file will locate): ");
        String output_dir = read_input();

        // input_dir = "data/시청 기록.html";
        // output_dir = "output";
        SimpleReader in = new SimpleReader1L(input_dir); 

        // initialization of data storage
        Sequence<Queue<String>> view_history = new Sequence1L<Queue<String>>();
        init_queues(view_history);

        extract_view_history(in, view_history); // extract youtube data

        SimpleWriter out = new SimpleWriter1L(output_dir + "/youtube_view_history.csv");
        make_csv(out, view_history); // print data into text file
        /*
         * Close input and output streams
         */
        in.close();
        out.close();
    }

}
