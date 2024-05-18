import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

@WebServlet(name = "SearchSuggestionServlet", urlPatterns = "/api/searchSuggestion")
public class SearchSuggestionServlet extends HttpServlet {
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            JsonArray jsonArray = new JsonArray();
            // get the query string from parameter
            String title = request.getParameter("query");
            String[] titleTerms = title.split("\\s+");
            StringBuilder fullTextQuery = new StringBuilder();
            for (String term : titleTerms) {
                fullTextQuery.append(term).append("* ");
            }
            title = fullTextQuery.toString().trim();

            // return the empty json array if query is null or empty
            if (title == null || title.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }

            // TODO: in project 4, you should do full text search with MySQL to find the matches on movies and stars
            try (Connection conn = dataSource.getConnection()) {
                String querySuggestions = "SELECT DISTINCT movies.id, movies.title, movies.year, movies.director " +
                        "FROM movies " +
                        "WHERE MATCH(title) AGAINST(? IN BOOLEAN MODE) LIMIT 10";

                PreparedStatement statement = conn.prepareStatement(querySuggestions);
                statement.setString(1, title);
                ResultSet suggestions = statement.executeQuery();

                while (suggestions.next()) {
                    JsonObject suggestion = generateJsonObject(suggestions.getString("title"), suggestions.getString("year"),suggestions.getString("id"));
                    jsonArray.add(suggestion);
                }
            }

            response.getWriter().write(jsonArray.toString());
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }
    }

    private static JsonObject generateJsonObject(String title, String year, String id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", title);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("year", year);
        additionalDataJsonObject.addProperty("id", id);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }
}