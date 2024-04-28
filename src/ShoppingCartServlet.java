import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

import java.util.ArrayList;
import java.util.List;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "ShoppingCartServlet", urlPatterns = "/api/shoppingCart")
public class ShoppingCartServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        // Retrieve user information from the session
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        JsonArray jsonArray = new JsonArray();
        ShoppingCart shoppingCart = user.getShoppingCart();
        List<Item> items = shoppingCart.getItems();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("Title", item.getName());
            jsonObject.addProperty("Price", item.getPrice());
            jsonObject.addProperty("Quantity", item.getQuantity());
            jsonObject.addProperty("Total", item.getTotal());
            jsonArray.add(jsonObject);
        }

        // Write JSON string to output
        out.write(jsonArray.toString());
        // Set response status to 200 (OK)
        response.setStatus(200);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String itemName = request.getParameter("title");
        String itemPriceString = request.getParameter("price");

        if ("delete".equals(itemPriceString)) {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            ShoppingCart shoppingCart = user.getShoppingCart();
            Item item = new Item(itemName, 1, 1);
            shoppingCart.deleteItem(item);
        }
        else {
            int itemPrice = Integer.parseInt(itemPriceString);

            if (itemPrice >= 0) {
                // Retrieve user information (assuming it's stored in the session)
                HttpSession session = request.getSession();
                User user = (User) session.getAttribute("user");

                ShoppingCart shoppingCart = user.getShoppingCart();
                Item item = new Item(itemName, itemPrice, 1);
                shoppingCart.addItem(item);
            }
            else {
                // Retrieve user information (assuming it's stored in the session)
                HttpSession session = request.getSession();
                User user = (User) session.getAttribute("user");

                ShoppingCart shoppingCart = user.getShoppingCart();
                Item item = new Item(itemName, itemPrice, 1);
                shoppingCart.subtractItem(item);

            }
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.getWriter().write("{\"success\": true}");
    }
}