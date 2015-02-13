import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

/**
 * Workset Member Metadata Model Object.
 *
 * <P>Representations of Workset Member Metadata
 *
 * @author Colleen Fallaw
 * @version 0.1
 */
public class WorksetMemberMetadata {

    /**
     * Returns a W3C Document with root element <response> a <doc> child for each id,
     *
     * @param ids           List<String>    list of volume id strings to get metadata about
     * @param solrApiClient SolrApiClient   object that connects with HTRC's Solr Proxy API
     * @param fieldList     String          list of fields to be included, with a comma but no space in between values
     * @return              Document        root element <response> a <doc> child for each id
     */
    public Document getXmlFromSolr(List<String> ids, SolrApiClient solrApiClient, String fieldList) {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        DOMImplementation domImplementation = docBuilder.getDOMImplementation();

        Document returnDoc = docBuilder.newDocument();
        Document responseDoc = null;
        NodeList tempNodeList = null;
        Node tempNode = null;
        Element rootElement = returnDoc.createElement("response");
        returnDoc.appendChild(rootElement);


        for (String id : ids) {

            String query =  "id:" + id;

            System.out.println(id);

            responseDoc=solrApiClient.getResponseDoc(query, fieldList);
            if (responseDoc != null) {
                tempNodeList = responseDoc.getElementsByTagName("doc");
                Node n = tempNodeList.item(0);
                if (n != null) {
                    tempNode = returnDoc.importNode(n, true);
                    returnDoc.getDocumentElement().appendChild(tempNode);
                } else {
                    System.out.println("could not get response for " + id);
                }
           }
        }

        return returnDoc;
    }

    /**
     * Returns a String representation of a W3C Document with root element <response> a <doc> child element for each id
     * @param ids           List<String>    list of volume id strings to get metadata about
     * @param client        SolrApiClient   object that connects with HTRC's Solr Proxy API
     * @param fieldList     String          list of fields to be included, with a comma but no space in between values
     * @return              String          W3C Document with root element <response> a <doc> child  for each id
     */

    public String getXmlStringFromSolr(List<String> ids, SolrApiClient client, String fieldList) {
        return getStringFromDoc(getXmlFromSolr(ids, client, fieldList));
    }


    /**
     * Returns a JSON representation of a W3C Document with root element <response> a <doc> child element for each id
     * @param ids           List<String>    list of volume id strings to get metadata about
     * @param client        SolrApiClient   object that connects with HTRC's Solr Proxy API
     * @param fieldList     String          list of fields to be included, with a comma but no space in between values
     * @return              String          JSON conversion of Document with root element <response> a <doc> child for each id
     */
    public String getOneJsonStringFromSolr(List<String> ids, SolrApiClient client, String fieldList) {

        final int PRETTY_PRINT_INDENT_FACTOR = 4;

        String xmlString = getXmlStringFromSolr(ids, client, fieldList);
        String jsonString = "error";

        try {
            JSONObject xmlJSONObj = XML.toJSONObject(xmlString);
            String jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
            System.out.println(jsonPrettyPrintString);
            jsonString = jsonPrettyPrintString;
        } catch (JSONException je) {
            jsonString = je.toString();
            }
        //System.out.print(jsonString);
        return jsonString;
    }

    /**
     *
     * @param doc   Document    W3C Document
     * @return      String      Formatted XML String
     */
    public String getStringFromDoc(Document doc) {
        try {
            final Document document = doc;

            OutputFormat format = new OutputFormat(document);
            format.setLineWidth(65);
            format.setIndenting(true);
            format.setIndent(2);
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);

            return out.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
