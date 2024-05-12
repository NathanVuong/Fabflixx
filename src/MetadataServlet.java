import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


// Declaring a WebServlet called MovieListServlet, which maps to url "/api/moviel=List"
@WebServlet(name = "MetadataServlet", urlPatterns = "/api/metadata")
public class MetadataServlet extends HttpServlet {
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
        session.setAttribute("result", "employee-index.html");


        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {


            // Declare our statement
            Statement statementOne = conn.createStatement();


            // Get top 20 movies
            String queryMetadata = "SELECT TABLE_NAME, COLUMN_NAME, COLUMN_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'moviedb';";
            ResultSet metadata = statementOne.executeQuery(queryMetadata);
            JsonArray jsonArray = new JsonArray();


            while (metadata.next()) {
                // Retrieve title, year, director, and rating from top movies
                String table_name = metadata.getString("TABLE_NAME");
                String attribute_name = metadata.getString("COLUMN_NAME");
                String attribute_type = metadata.getString("COLUMN_TYPE");


                // Create a JsonObject based on the data we retrieve
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("table_name", table_name);
                jsonObject.addProperty("attribute_name", attribute_name);
                jsonObject.addProperty("attribute_type", attribute_type);

                jsonArray.add(jsonObject);
            }
            metadata.close();
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
