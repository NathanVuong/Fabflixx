import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;




// Declaring a WebServlet called MovieListServlet, which maps to url "/api/moviel=List"
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movieList")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;


    // Create a dataSource which registered in web.
    private DataSource dataSource;


    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }


    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json"); // Response mime type


        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // set this user into the session
        HttpSession session = request.getSession();
        session.setAttribute("result", "movie-list.html");


        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {


            // Declare our statement
            Statement statementOne = conn.createStatement();


            // Get top 20 movies
            String queryTopMovies = "SELECT DISTINCT movies.id, movies.title, movies.year, movies.director, ratings.rating, movies.price FROM movies LEFT JOIN ratings ON movies.id = ratings.movieId ORDER BY ratings.rating DESC LIMIT 20;";
            ResultSet topMovies = statementOne.executeQuery(queryTopMovies);
            JsonArray jsonArray = new JsonArray();


            while (topMovies.next()) {
                // Retrieve title, year, director, and rating from top movies
                String movie_title = topMovies.getString("title");
                String movie_year = topMovies.getString("year");
                String movie_director = topMovies.getString("director");
                String movie_rating = topMovies.getString("rating");
                String movie_price = topMovies.getString("price");


                // Retrieve 3 genres and 3 stars max for top movies
                String movie_id = topMovies.getString("id");
                String queryGenres = "SELECT genres.name FROM movies JOIN genres_in_movies ON genres_in_movies.movieId = movies.id JOIN genres ON genres.id = genres_in_movies.genreId WHERE movies.id = ? ORDER BY genres.name ASC LIMIT 3;";
                PreparedStatement statementTwo = conn.prepareStatement(queryGenres);
                statementTwo.setString(1, movie_id);
                ResultSet genres = statementTwo.executeQuery();
                String queryStars = "SELECT stars.name, stars.id FROM movies JOIN stars_in_movies ON stars_in_movies.movieId = movies.id JOIN stars ON stars.id = stars_in_movies.starId WHERE movies.id = ? ORDER BY stars.name ASC LIMIT 3;";
                PreparedStatement statementThree = conn.prepareStatement(queryStars);
                statementThree.setString(1, movie_id);
                ResultSet stars = statementThree.executeQuery();


                // Create a JsonObject based on the data we retrieve
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_rating", movie_rating);
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_price", movie_price);


                int genre_count = 1;
                while (genres.next()) {
                    String curr_genre = genres.getString("name");
                    jsonObject.addProperty("movie_genre_" + Integer.toString(genre_count), curr_genre);
                    genre_count++;
                }
                genres.close();
                statementTwo.close();


                int star_count = 1;
                while (stars.next()) {
                    String curr_star = stars.getString("name");
                    String curr_id = stars.getString("id");
                    jsonObject.addProperty("movie_star_" + Integer.toString(star_count), curr_star);
                    jsonObject.addProperty("movie_star_id_" + Integer.toString(star_count), curr_id);
                    star_count++;
                }
                stars.close();
                statementThree.close();


                jsonArray.add(jsonObject);
            }
            topMovies.close();
            statementOne.close();


            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");


            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);
        } catch (Exception e) {


            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());


            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }


        // Always remember to close db connection after usage. Here it's done by try-with-resources


    }
}
