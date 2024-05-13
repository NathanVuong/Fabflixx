package XMLData;
import java.util.ArrayList;
import java.util.List;

public class Cast {
    private String movidId;
    private List<String> actors;

    public Cast(){actors = new ArrayList<String>();}

    public void setMovidId(String movidId) {this.movidId = movidId;}
    public void addActor(String actor) {this.actors.add(actor);}

    public String getMovidId() {return movidId;}
    public List<String> getActors() {return actors;}

    public String toString() {return "movidId: " + movidId + ", actors: " + actors;}
}
