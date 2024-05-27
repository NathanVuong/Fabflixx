import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.CallableStatement;
import java.sql.Types;

@WebServlet(name = "AddMovieServlet", urlPatterns = "/api/addMovie")
public class AddMovieServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    private static final long serialVersionUID = 2L;


    // Create a dataSource which registered in web.xml
    private DataSource dataSource;


    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/writedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String movieName = request.getParameter("movie-name");
        String director = request.getParameter("director");
        String year = request.getParameter("movie-year");
        String genre = request.getParameter("genre");
        String starName = request.getParameter("star-name");
        String birthYear = request.getParameter("birth-year");
        System.out.println(movieName);
        System.out.println(director);
        System.out.println(year);
        System.out.println(genre);
        System.out.println(starName);
        System.out.println(birthYear);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        JsonObject responseJsonObject = new JsonObject();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Check if the movie already exists
            boolean movieExists = false;
            try (PreparedStatement checkStatement = conn.prepareStatement("SELECT 1 FROM movies WHERE title = ? AND year = ? AND director = ?")) {
                checkStatement.setString(1, movieName);
                checkStatement.setString(2, year);
                checkStatement.setString(3, director);
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        movieExists = true;
                    }
                }

            }

            if (movieExists) {
                // Movie already exists, add a "failed" property to the response
                responseJsonObject.addProperty("status", "failed");
            } else {
                // Movie doesn't exist, proceed with the stored procedure call
                System.out.println("Before procedure call");
                try (CallableStatement statement = conn.prepareCall("{call add_movie(?, ?, ?, ?, ?, ?, ?, ?, ?)}")) {
                    // Set the parameters for the stored procedure
                    System.out.println("After procedure call");
                    statement.setString(1, movieName);
                    statement.setInt(2, Integer.parseInt(year));
                    statement.setString(3, director);
                    statement.setString(4, starName);
                    if (!birthYear.isEmpty()) {
                        statement.setInt(5, Integer.parseInt(birthYear));
                    } else {
                        statement.setNull(5, Types.INTEGER);
                    }
                    statement.setString(6, genre);
                    System.out.println("Before register out params");

                    // Register output parameters
                    statement.registerOutParameter(7, Types.VARCHAR); // movie_id_out
                    System.out.println("movie id out");
                    statement.registerOutParameter(8, Types.VARCHAR); // star_id_out
                    System.out.println("star id out");
                    statement.registerOutParameter(9, Types.INTEGER); // genre_id_out
                    System.out.println("Inside SP");
                    // Execute the stored procedure
                    statement.execute();
                    System.out.println("Executed SP");
                    // Retrieve output parameters
                    String movieId = statement.getString(7);
                    String starId = statement.getString(8);
                    int genreId = statement.getInt(9);
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("movieId", movieId);
                    responseJsonObject.addProperty("starId", starId);
                    responseJsonObject.addProperty("genreId", String.valueOf(genreId));
                }
            }
            response.getWriter().write(responseJsonObject.toString());
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
    }
}
