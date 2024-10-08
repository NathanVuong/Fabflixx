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
@WebServlet(name = "SearchResultsServlet", urlPatterns = "/api/searchMovie")
public class SearchResultsServlet extends HttpServlet {
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

        PrintWriter out = response.getWriter();

        String genre = request.getParameter("genre");
        String title = request.getParameter("title");
        if (title != null && !title.isEmpty()) {
            String[] titleTerms = title.split("\\s+");
            StringBuilder fullTextQuery = new StringBuilder();
            for (String term : titleTerms) {
                fullTextQuery.append(term).append("* ");
            }
            title = fullTextQuery.toString().trim();
        }
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String star = request.getParameter("star");
        String movieNumber = request.getParameter("movie_number");
        String order = request.getParameter("order");
        String page = request.getParameter("page");

        //get session and update result
        HttpSession session = request.getSession();
        String resultURL = "search-result.html?title=" + title + "&year=" + year + "&director" + director +
                "&star" + star + "&movie_number=" + movieNumber + "&order=" + order + "&page=" + page;
        session.setAttribute("result", resultURL);

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Declare our statement
            String queryTopMovies = "SELECT DISTINCT movies.id, movies.title, movies.year, movies.director, ratings.rating, movies.price " +
                    "FROM movies " +
                    "LEFT JOIN ratings ON movies.id = ratings.movieId " +
                    "JOIN genres_in_movies ON movies.id = genres_in_movies.movieId " +
                    "JOIN genres ON genres_in_movies.genreId = genres.id " +
                    "JOIN stars_in_movies ON movies.id = stars_in_movies.movieId " +
                    "JOIN stars ON stars_in_movies.starId = stars.id " +
                    "WHERE 1=1";
            if (genre != null && !genre.isEmpty()) {
                queryTopMovies += " AND genres.name = ?";
            }
            if (title != null && !title.isEmpty()) {
                queryTopMovies += " AND MATCH(title) AGAINST(? IN BOOLEAN MODE)";
            }
            if (year != null && !year.isEmpty()) {
                queryTopMovies += " AND movies.year = ?";
            }
            if (director != null && !director.isEmpty()) {
                queryTopMovies += " AND movies.director LIKE ?";
            }
            if (star != null && !star.isEmpty()) {
                queryTopMovies += " AND stars.name LIKE ?";
            }

            // Implement ordering and limiting
            String first_sort = "";
            String first_sort_order = "";
            String second_sort = "";
            String second_sort_order = "";
            //checking to see the ordering
            if (order.charAt(0) == 't'){
                first_sort = "movies.title";
                second_sort = "ratings.rating";
            } else {
                first_sort = "ratings.rating";
                second_sort = "movies.title";
            }
            if (order.charAt(1) == 'a'){
                first_sort_order = "ASC";
            } else {
                first_sort_order = "DESC";
            }
            if (order.charAt(3) == 'a'){
                second_sort_order = "ASC";
            } else {
                second_sort_order = "DESC";
            }

            //Get offset by using page number and movie number
            String offset_value = String.valueOf(Integer.parseInt(movieNumber) * (Integer.parseInt(page)-1));
            //this is to see if there is a next
            String offset_value_future = String.valueOf(Integer.parseInt(movieNumber) * (Integer.parseInt(page)));

            String queryCheckFuture = queryTopMovies + " ORDER BY ? ?, ? ? LIMIT ? OFFSET ?;";
            PreparedStatement statementOne = conn.prepareStatement(queryCheckFuture);

            int parameterIndex = 1;

            if (genre != null && !genre.isEmpty()) {
                statementOne.setString(parameterIndex++, genre);
            }
            if (title != null && !title.isEmpty()) {
                statementOne.setString(parameterIndex++, title);
            }
            if (year != null && !year.isEmpty()) {
                statementOne.setString(parameterIndex++, year);
            }
            if (director != null && !director.isEmpty()) {
                statementOne.setString(parameterIndex++, "%" + director + "%");
            }
            if (star != null && !star.isEmpty()) {
                statementOne.setString(parameterIndex++, "%" + star + "%");
            }

            statementOne.setString(parameterIndex++, first_sort);
            statementOne.setString(parameterIndex++, first_sort_order);
            statementOne.setString(parameterIndex++, second_sort);
            statementOne.setString(parameterIndex++, second_sort_order);
            statementOne.setInt(parameterIndex++, Integer.parseInt(movieNumber));
            statementOne.setInt(parameterIndex, Integer.parseInt(offset_value_future));

            JsonObject jsonObject0 = new JsonObject();

            ResultSet futureMovies = statementOne.executeQuery();
            if (!futureMovies.next()) {
                jsonObject0.addProperty("future", "false");
            } else {
                jsonObject0.addProperty("future", "true");
            }

            queryTopMovies += " ORDER BY ? ?, ? ? LIMIT ? OFFSET ?;";
            statementOne = conn.prepareStatement(queryTopMovies);

            parameterIndex = 1;

            if (genre != null && !genre.isEmpty()) {
                statementOne.setString(parameterIndex++, genre);
            }
            if (title != null && !title.isEmpty()) {
                statementOne.setString(parameterIndex++, "%" + title + "%");
            }
            if (year != null && !year.isEmpty()) {
                statementOne.setString(parameterIndex++, year);
            }
            if (director != null && !director.isEmpty()) {
                statementOne.setString(parameterIndex++, "%" + director + "%");
            }
            if (star != null && !star.isEmpty()) {
                statementOne.setString(parameterIndex++, "%" + star + "%");
            }

            statementOne.setString(parameterIndex++, first_sort);
            statementOne.setString(parameterIndex++, first_sort_order);
            statementOne.setString(parameterIndex++, second_sort);
            statementOne.setString(parameterIndex++, second_sort_order);
            statementOne.setInt(parameterIndex++, Integer.parseInt(movieNumber));
            statementOne.setInt(parameterIndex, Integer.parseInt(offset_value));

            ResultSet topMovies = statementOne.executeQuery();

            JsonArray jsonArray = new JsonArray();
            jsonArray.add(jsonObject0);

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
