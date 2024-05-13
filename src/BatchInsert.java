import java.sql.*;

import XMLData.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;


public class BatchInsert {

    public static void main (String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        XMLParserMain spe = new XMLParserMain();
        spe.runExample();
        List<Movie> newMovies = spe.getNewMovies();
        //System.out.println(newMovies.size());

        XMLParserActors spe2 = new XMLParserActors();
        spe2.runExample();
        List<Star> newStars = spe2.getNewStars();

        XMLParserCast spe3 = new XMLParserCast();
        spe3.runExample();
        List<Cast> newCasts = spe3.getNewCasts();

        HashMap<String, String> starToId = new HashMap<String, String>();
        HashMap<String, String> idToMovie = new HashMap<String, String>();



        Connection conn = null;

        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        String jdbcURL="jdbc:mysql://localhost:3306/moviedb";

        try {
            conn = DriverManager.getConnection(jdbcURL,"mytestuser", "My6$Password");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PreparedStatement psInsertRecord=null;
        String sqlInsertRecord=null;

        int[] iNoRows=null;

        // Use for movie inserts
        sqlInsertRecord="insert ignore into movies (id, title, year, director) values(?,?,?,?)";
        try {
            conn.setAutoCommit(false);

            psInsertRecord=conn.prepareStatement(sqlInsertRecord);


            for(Movie m: newMovies) {
                psInsertRecord.setString(1, m.getId());
                psInsertRecord.setString(2,m.getTitle());
                psInsertRecord.setInt(3, m.getYear());
                psInsertRecord.setString(4, m.getDirector());
                psInsertRecord.addBatch();
                idToMovie.put(m.getId(), m.getTitle());
            }

            iNoRows=psInsertRecord.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Use for genres inserts
        sqlInsertRecord="insert ignore into genres (name) values(?)";
        try {
            conn.setAutoCommit(false);

            psInsertRecord=conn.prepareStatement(sqlInsertRecord);


            for(Movie m: newMovies) {
                for (String genre: m.getGenres()) {
                    psInsertRecord.setString(1, genre);
                    psInsertRecord.addBatch();
                }
            }

            iNoRows=psInsertRecord.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }


        String getAllGenres = "select id, name from genres";
        HashMap<String, Integer> GenreMap = new HashMap<String, Integer>();
        try {
            PreparedStatement ps = conn.prepareStatement(getAllGenres);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                GenreMap.put(rs.getString("name"), rs.getInt("id"));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //System.out.println(GenreMap);
        System.out.print("test");


        //Use for Genres in Movies
        sqlInsertRecord="insert ignore into genres_in_movies (genreId, movieId) values(?,?)";
        try {
            conn.setAutoCommit(false);

            psInsertRecord=conn.prepareStatement(sqlInsertRecord);


            for(Movie m: newMovies) {
                for (String genre: m.getGenres()) {
                    psInsertRecord.setInt(1, GenreMap.get(genre));
                    psInsertRecord.setString(2, m.getId());
                    psInsertRecord.addBatch();
                }
            }

            iNoRows=psInsertRecord.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Use for actor inserts
        sqlInsertRecord="insert ignore into stars (id, name, birthYear) values(?,?,?)";
        try {

            int startIncr = 9423080;

            conn.setAutoCommit(false);

            psInsertRecord=conn.prepareStatement(sqlInsertRecord);


            for(Star s: newStars) {
                startIncr = startIncr + 1;
                String newId = "nm" + startIncr;
                psInsertRecord.setString(1, newId);
                psInsertRecord.setString(2,s.getName());
                psInsertRecord.setInt(3, s.getBirthYear());
                psInsertRecord.addBatch();
                starToId.put(s.getName(), newId);
            }

            iNoRows=psInsertRecord.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Use for cast
        sqlInsertRecord="insert ignore into stars_in_movies (starId, movieId) values(?,?)";
        try {
            conn.setAutoCommit(false);

            psInsertRecord=conn.prepareStatement(sqlInsertRecord);

            for(Cast c: newCasts) {
                for (String actor: c.getActors()) {
                    String starId = starToId.get(actor);
                    if (!(starId == null) && !(idToMovie.get(c.getMovidId()) == null)){
                        psInsertRecord.setString(1, starId);
                        psInsertRecord.setString(2,c.getMovidId());
                        psInsertRecord.addBatch();
                    }

                }

            }

            iNoRows=psInsertRecord.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if(psInsertRecord!=null) psInsertRecord.close();
            if(conn!=null) conn.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

}


