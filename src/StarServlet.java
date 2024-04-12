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
@WebServlet(name = "MovieServlet", urlPatterns = "/api/single-star")
public class StarServlet extends HttpServlet {
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
            String queryStars = "SELECT stars.name, stars.birthYear FROM stars WHERE stars.id = ?;";
            String queryMovies = "SELECT movies.title, movies.id FROM movies JOIN stars_in_movies ON stars_in_movies.moviesId = movies.id WHERE stars_in_movies.starsId = ?;";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(queryStars);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);

            // Perform the first query to get name and birth year
            ResultSet rs = statement.executeQuery();
            rs.first();
            String star_name = rs.getString("name");
            String star_birth_year= rs.getString("birthYear");

            // Close it
            rs.close();
            statement.close();

            // Create first JSON object with this
            JsonObject jsonObjectOne = new JsonObject();
            jsonObjectOne.addProperty("star_name", star_name);
            jsonObjectOne.addProperty("star_birth_year", star_birth_year);
            jsonArray.add(jsonObjectOne);

            // Perform second query to get movies
            statement = conn.prepareStatement(queryMovies);
            statement.setString(1, id);
            rs = statement.executeQuery();

            // Second JSON object will have all movies star was in
            JsonObject jsonObjectTwo = new JsonObject();
            int movieCount = 0;
            while (rs.next()) {
                String movie = rs.getString("name");
                String movie_id = rs.getString("id");
                jsonObjectTwo.addProperty("movie_" + Integer.toString(movieCount), movie);
                jsonObjectTwo.addProperty("movie_id_" + Integer.toString(movieCount), movie_id);
                movieCount++;
            }
            jsonObjectTwo.addProperty("movie_count", String(movieCount));
            jsonArray.add(jsonObjectTwo);
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