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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet(name = "AddStarServlet", urlPatterns = "/api/addStar")
public class AddStarServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String starName = request.getParameter("star-name");
        String birthYear = String.valueOf(request.getParameter("birth-year"));
        String newStarId = "";
        System.out.println(starName);
        System.out.println(birthYear);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            String addStarQuery = "";
            PreparedStatement statement;

            String newStarIdQuery = "SELECT CONCAT('nm', LPAD(IFNULL(MAX(CAST(SUBSTRING(id, 3) AS UNSIGNED)), 0) + 1, 7, '0')) AS new_id FROM stars;";
            PreparedStatement statementId = conn.prepareStatement((newStarIdQuery));
            ResultSet rs = statementId.executeQuery();
            while (rs.next()) {
                newStarId = rs.getString("new_id");
            }
            System.out.println(newStarId);
            rs.close();
            statementId.close();

            if (birthYear.isEmpty()) {
                addStarQuery = "INSERT INTO stars (id, name) VALUES (?, ?)";
                statement = conn.prepareStatement(addStarQuery);
                statement.setString(1, newStarId);
                statement.setString(2, starName);
            }
            else {
                addStarQuery = "INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?)";
                statement = conn.prepareStatement(addStarQuery);
                statement.setString(1, newStarId);
                statement.setString(2, starName);
                statement.setString(3, birthYear);
            }
            System.out.println("Before query");
            int rowsAffected = statement.executeUpdate();
            System.out.println("After query");
            statement.close();

            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("starId", newStarId);

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
