import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.time.LocalDate;

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
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
        String firstName = request.getParameter("first-name");
        String lastName = request.getParameter("last-name");
        String creditCard = request.getParameter("credit-card");
        String date = request.getParameter("date");

        System.out.println(firstName + " " + lastName + " " + creditCard + " " + date);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Get username and password from database

            // Get password given username
            String queryUsername = "SELECT DISTINCT creditcards.id, creditcards.firstName, creditcards.lastName, creditcards.expiration" +
                    " FROM creditcards WHERE creditcards.id = ? AND creditcards.firstName = ? AND creditcards.lastName = ? AND creditcards.expiration = ?;";
            PreparedStatement statement = conn.prepareStatement(queryUsername);
            statement.setString(1, creditCard);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, date);
            System.out.println(statement);
            ResultSet resultSet = statement.executeQuery();

            JsonObject responseJsonObject = new JsonObject();
            if (resultSet.next()) {
                responseJsonObject.addProperty("status", "success");
                System.out.println("confirmed");
                resultSet.close();
                statement.close();
                //Adding to the sales

                // Retrieve user information from the session
                HttpSession session = request.getSession();
                User user = (User) session.getAttribute("user");
                int customerId = user.getUserId();

                Date currentDate = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String currentDateTransform = formatter.format(currentDate);

                System.out.println("current date: " + currentDateTransform);

                //Get all items
                List<Item> itemsList = user.getShoppingCart().getItems();
                for (int i = 0; i < itemsList.size(); i++){
                    Item currentItem = itemsList.get(i);
                    PreparedStatement forInsert = conn.prepareStatement("INSERT INTO sales(customerId,movieId,saleDate,quantity) " +
                            "VALUES (?, ?, ?, ?)");
                    forInsert.setInt(1, customerId);
                    forInsert.setString(2, currentItem.getMovieId());
                    forInsert.setDate(3, java.sql.Date.valueOf(currentDateTransform));
                    forInsert.setInt(4, currentItem.getQuantity());
                    System.out.println(forInsert);
                    forInsert.executeUpdate();
                    forInsert.close();
                }
            } else {
                responseJsonObject.addProperty("status", "error");
                System.out.println("failed");
                resultSet.close();
                statement.close();
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
