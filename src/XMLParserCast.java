
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import XMLData.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class XMLParserCast extends DefaultHandler {

    List<Cast> newCasts;


    private String tempVal;

    //to maintain context
    private Cast tempEmp;

    public XMLParserCast() {
        newCasts = new ArrayList<Cast>();
    }

    public void runExample() {
        parseDocument();
        //printData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("src/stanford-movies/casts124.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printData() {


        Iterator<Cast> it = newCasts.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
        System.out.println("No of Stars '" + newCasts.size() + "'.");
    }

    public List<Cast> getNewCasts() {return newCasts;}

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("filmc")) {
            tempEmp = new Cast();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        //System.out.println(iter + ": " + tempVal);
        //iter = iter + 1;

        if (qName.equalsIgnoreCase("f")) {
            if(tempVal.length() < 10) {
                String toAdd = "";
                for (int i = tempVal.length(); i < 10; i++) {
                    toAdd = toAdd + "0";
                }
                tempVal = toAdd + tempVal;
            }
            tempEmp.setMovidId(tempVal);
        } else if (qName.equalsIgnoreCase("a")) {
            tempEmp.addActor(tempVal);
        } else if (qName.equalsIgnoreCase("filmc")) {
            newCasts.add(tempEmp);
        }

    }

    public static void main(String[] args) {
        XMLParserCast spe = new XMLParserCast();
        spe.runExample();
    }

}
