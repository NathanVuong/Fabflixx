
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

public class XMLParserActors extends DefaultHandler {

    List<Star> newStars;

    private int iter = 0;
    private boolean validStar;

    private String tempVal;

    //to maintain context
    private Star tempEmp;
    private String curDirector;

    public XMLParserActors() {
        newStars = new ArrayList<Star>();
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
            sp.parse("src/stanford-movies/actors63.xml", this);

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


        Iterator<Star> it = newStars.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
        System.out.println("No of Stars '" + newStars.size() + "'.");
    }

    public List<Star> getNewStars() {return newStars;}

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("actor")) {
            tempEmp = new Star();
            validStar = true;
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        //System.out.println(iter + ": " + tempVal);
        //iter = iter + 1;

        if (qName.equalsIgnoreCase("stagename")) {
            tempEmp.setName(tempVal);
        } else if (qName.equalsIgnoreCase("dob")) {
            int middle = 0;
            try {
                middle = Integer.parseInt(tempVal);
                tempEmp.setBirthYear(middle);
            } catch(NumberFormatException e) {
                //
            } catch(NullPointerException e) {
                //
            }
        } else if (qName.equalsIgnoreCase("actor")) {
            if (validStar) {
                newStars.add(tempEmp);
            }
        }

    }

    public static void main(String[] args) {
        XMLParserActors spe = new XMLParserActors();
        spe.runExample();
    }

}
