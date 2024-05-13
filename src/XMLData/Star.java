package XMLData;

public class Star {
    private String name;
    private int birthYear;

    public Star() {}

    public void setName(String name) {this.name = name;}
    public void setBirthYear(int birthYear) {this.birthYear = birthYear;}

    public String getName() {return name;}
    public int getBirthYear() {return birthYear;}

    public String toString() {
        return "Star [name=" + name + ", birthYear=" + birthYear + "]";
    }
}
