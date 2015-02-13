import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Workset Member Metadata Controller.
 *
 * <P>Controls overall workflow for getting specified Solr Metadata fields for specified critera and fields
 *
 * @author Colleen Fallaw
 * @version 0.1
 */
public class Main {

    public static void main(String[] args) {

        //set default values

        String inputFile = "volumeIds.txt";
        String outputFile = "metadata.xml";
        String fieldList = "";//default of blank means all
        String endpoint = "http://chinkapin.pti.indiana.edu:9994/solr/meta/select/";

        if (args.length > 0) inputFile = args[0];
        if (args.length > 1) outputFile = args[1];
        if (args.length > 2) fieldList = args[2];
        if (args.length > 3) endpoint = args[3];

        SolrApiClient client = new SolrApiClient(endpoint);

        File out = new File(outputFile);
        FileWriter fw = null;

        // if output file does not exist,then create it
        if (!out.exists()) {
            try {
                out.createNewFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            //read ids from file into list
            ArrayList<String> idList = new ArrayList<String>();
            PrintWriter outWriter = new PrintWriter(outputFile);

            List<String> ids = FileUtils.readLines(new File(inputFile));

            for (String i : ids) {
                System.out.println(i);
                idList.add(i.trim());
            }

            WorksetMemberMetadata worksetMemberMetadata = new WorksetMemberMetadata();

            String doc = worksetMemberMetadata.getXmlStringFromSolr(ids, client, fieldList);

            //write the string to a file
            outWriter.write(doc);
            outWriter.close();

            System.out.print("done");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}