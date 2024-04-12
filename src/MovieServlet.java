import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "MovieServlet", urlPatterns = "/api/single-movie")
public class MovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // JSON array
            JsonArray jsonArray = new JsonArray();

            // Construct a queries with parameters represented by "?"
            String queryMovie = "SELECT movies.title, movies.director, movies.year FROM movies WHERE movies.id = ?;";
            String queryRating = "SELECT ratings.rating FROM ratings WHERE ratings.movieId = ?;";
            String queryGenres = "SELECT genres.name, genre.id FROM genres JOIN genres_in_movies ON genres_in_movies.genreId = genres.id WHERE genres_in_movies.movieId = ?;";
            String queryStars = "SELECT stars.name, stars.id FROM stars JOIN stars_in_movies ON stars_in_movies.starId = stars.id WHERE stars_in_movies.movieId = ?;";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(queryMovie);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);

            // Perform the first query to get title, director, and year
            ResultSet rs = statement.executeQuery();
            rs.first();
            String movie_title = rs.getString("title");
            String movie_director = rs.getString("director");
            String movie_year = rs.getString("year");

            // Close it
            rs.close();
            statement.close();

            // Perform second query to get rating
            statement = conn.prepareStatement(queryRating);
            statement.setString(1, id);
            rs = statement.executeQuery();
            String movie_rating = rs.getString("rating");
            rs.close();
            statement.close();

            // First JSON object will have these four elements
            JsonObject jsonObjectOne = new JsonObject();
            jsonObjectOne.addProperty("movie_title", movie_title);
            jsonObjectOne.addProperty("movie_director", movie_director);
            jsonObjectOne.addProperty("movie_year", movie_year);
            jsonObjectOne.addProperty("rating", movie_rating);
            jsonArray.add(jsonObjectOne);

            // Perform third query to get genres
            statement = conn.prepareStatement(queryGenres);
            statement.setString(1, id);
            rs = statement.executeQuery();

            // Second JSON object will have all genres
            JsonObject jsonObjectTwo = new JsonObject();
            int genreCount = 0;
            while (rs.next()) {
                String genre = rs.getString("name");
                String genre_id = rs.getString("id");
                jsonObjectTwo.addProperty("genre_" + Integer.toString(genreCount), genre);
                jsonObjectTwo.addProperty("genre_id_" + Integer.toString(genreCount), genre_id);
                genreCount++;
            }
            jsonObjectTwo.addProperty("genre_count", String(genreCount));
            jsonArray.add(jsonObjectTwo);
            rs.close();
            statement.close();

            // Perform fourth query to get stars
            statement = conn.prepareStatement(queryStars);
            statement.setString(1, id);
            rs = statement.executeQuery();

            // Third JSON object will have all stars
            JsonObject jsonObjectThree = new JsonObject();
            int starCount = 0;
            while (rs.next()) {
                String star = rs.getString("name");
                String star_id = rs.getString("id");
                jsonObjectThree.addProperty("star_" + Integer.toString(starCount), star);
                jsonObjectThree.addProperty("star_id_" + Integer.toString(starCount), star_id);
                starCount++;
            }
            jsonObjectThree.addProperty("genre_count", String(starCount));
            jsonArray.add(jsonObjectThree);
            rs.close();
            statement.close();

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }

}