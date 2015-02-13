import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import static java.net.URLEncoder.encode;

/**
 * Solr API Client Model Object.
 *
 * <P>Methods for Interacting with HTRC's Solr Proxy API
 *
 * @author Colleen Fallaw
 * @version 0.1 - very incomplete at this point
 */
public class SolrApiClient {

    private String endpoint;

    private DocumentBuilderFactory docFactory;

    public SolrApiClient(String endpoint) {
        this.endpoint = endpoint;
        this.docFactory = DocumentBuilderFactory.newInstance();
    }

    // get the response xml document for a given query and fieldList

    /**
     *
     * @param query
     * @param fieldList
     * @return
     */
    public Document getResponseDoc(String query, String fieldList) {

        Document doc = null;

        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
            String charset = "UTF-8";

            String queryString = ("?q=" + encode(query.replace(':', '+').replace('/', '='), charset) );
            if (fieldList.length() > 0) { queryString = queryString + "&fl=" + fieldList;}

            URL url = new URL(this.endpoint + queryString );
            URLConnection connection =url.openConnection();
            connection.setRequestProperty("Accept-Charset", charset);
            InputStream response = connection.getInputStream();
            doc = docBuilder.parse(response);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return doc;
    }

    // get a comma separated value list of ids for a given query

    /**
     *
     * @param query
     * @return
     */
    public String getCommaSeparatedValueList(String query) {

        String returnString = "";
        Document doc = getResponseDoc(query, "id");
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodes = null;
        try {
            nodes = (NodeList)xPath.evaluate("//str[@name = 'id']", doc.getDocumentElement(), XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < nodes.getLength(); ++i) {
            Element e = (Element) nodes.item(i);
            returnString += e.getTextContent() + "\n";
        }

        return returnString;
    }

    // return the number of results for a given query

    /**
     *
     * @param query
     * @return
     */
    public int getCount(String query) {
        String returnString = "0";

        try {
            Document doc = getResponseDoc(query, "id");
            XPath xPath = XPathFactory.newInstance().newXPath();

            NodeList nodes = (NodeList)xPath.evaluate("//result", doc.getDocumentElement(), XPathConstants.NODESET);

            for (int i = 0; i < nodes.getLength(); ++i) {
                Element e = (Element) nodes.item(i);
                returnString = e.getAttribute("numFound");
            }


        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return Integer.parseInt(returnString);
    }

}