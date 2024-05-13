
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

public class XMLParserMain extends DefaultHandler {

    List<Movie> newMovies;

    private int iter = 0;
    private boolean validMovie;

    private String tempVal;

    private int invalidCount = 0;
    private int noIdCount = 0;
    private int noTitleCount = 0;
    private int noYearCount = 0;
    private boolean inDirectorFilms = false;
    private boolean inFilms = false;
    private boolean inFilM = false;


    //to maintain context
    private Movie tempEmp;
    private String curDirector;

    public XMLParserMain() {
        newMovies = new ArrayList<Movie>();
    }

    public void runExample() {
        parseDocument();
        printData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("src/stanford-movies/mains243.xml", this);

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
        System.out.println(invalidCount + " movies inconsistent.");
        System.out.println(noIdCount + " movies missing ids.");
        System.out.println(noTitleCount + " movies missing titles.");
        System.out.println(noYearCount + " movies missing years.");
    }

    public List<Movie> getNewMovies() {return newMovies;}

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("directorfilms")) {
            //System.out.println("directorfilms");
            inDirectorFilms = true;
        }
        if (qName.equalsIgnoreCase("films")) {
            if (inDirectorFilms) {
                //System.out.println("films");
                inFilms = true;
            }
        }
        if (qName.equalsIgnoreCase("film")) {
            tempEmp = new Movie();
            tempEmp.setDirector(curDirector);
            validMovie = true;
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        //System.out.println(iter + ": " + tempVal);
        //iter = iter + 1;

        if (qName.equalsIgnoreCase("dirname")) {
            if (tempVal.isEmpty()){
                validMovie = false;
            }
            curDirector = tempVal;
        } else if (qName.equalsIgnoreCase("fid")) {
            if (tempVal.isEmpty()) {
                validMovie = false;
                noIdCount++;
            }
            if(tempVal.length() < 10) {
                String toAdd = "";
                for (int i = tempVal.length(); i < 10; i++) {
                    toAdd = toAdd + "0";
                }
                tempVal = toAdd + tempVal;
            }
            tempEmp.setId(tempVal);
        } else if (qName.equalsIgnoreCase("t")) {
            if (tempVal.isEmpty() || tempVal.equals("NKT")) {
                validMovie = false;
                noTitleCount++;
            }
            tempEmp.setTitle(tempVal);
        } else if (qName.equalsIgnoreCase("cat")) {
            if (!tempVal.isEmpty()) {
                tempEmp.addGenre(tempVal);
            }
        } else if (qName.equalsIgnoreCase("year")) {
            int middle = 0;
            try {
                middle = Integer.parseInt(tempVal);
            } catch(NumberFormatException e) {
                validMovie = false;
                noYearCount++;
            } catch(NullPointerException e) {
                validMovie = false;
                noYearCount++;
            }
            if (middle < 1900 || middle > 2025){
                validMovie = false;
            }
            tempEmp.setYear(middle);
        } else if (qName.equalsIgnoreCase("film")) {
            if (validMovie && inFilms) {
                newMovies.add(tempEmp);
            } else {
                invalidCount += 1;
            }
        } else if (qName.equalsIgnoreCase("directorfilms")) {
            inDirectorFilms = false;
        } else if (qName.equalsIgnoreCase("films")) {
            inFilms = false;
        }

    }

    public static void main(String[] args) {
        XMLParserMain spe = new XMLParserMain();
        spe.runExample();
    }

}
