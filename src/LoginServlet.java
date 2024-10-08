import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.PrintWriter;

import org.jasypt.util.password.StrongPasswordEncryptor;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
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
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
            JsonObject responseJsonObject = new JsonObject();
            // Login fail
            responseJsonObject.addProperty("status", "fail");
            // Log to localhost log
            request.getServletContext().log("Captcha failed");
            // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
            responseJsonObject.addProperty("message", "Are you a robot?");
            response.getWriter().write(responseJsonObject.toString());
            return;
        }
        
        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Get username and password from database

            // Get password given username
            String queryUsername = "SELECT DISTINCT customers.password, customers.id FROM customers WHERE customers.email = ?;";
            PreparedStatement statement = conn.prepareStatement(queryUsername);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            String passwordFromQuery = "";
            int idFromQuery = 0;
            boolean success = false;
            if (resultSet.next()) {
                passwordFromQuery = resultSet.getString("password");
                idFromQuery = resultSet.getInt("id");
                success = new StrongPasswordEncryptor().checkPassword(password, passwordFromQuery);
                System.out.println("this works");
            }
            resultSet.close();
            statement.close();


            JsonObject responseJsonObject = new JsonObject();
            if (success) {
                // Login success:

                // set this user into the session
                request.getSession().setAttribute("user", new User(username, idFromQuery));

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

            } else {
                // Login fail
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Login failed");
                // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
                if (passwordFromQuery.isEmpty()) {
                    responseJsonObject.addProperty("message", "User " + username + " doesn't exist.");
                } else {
                    responseJsonObject.addProperty("message", "Incorrect Password");
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
