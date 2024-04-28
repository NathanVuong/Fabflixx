import java.util.ArrayList;
import java.util.List;

/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */
public class User {

    private final String username;
    private final ShoppingCart shoppingCart;

    public User(String username) {
        this.username = username;
        this.shoppingCart = new ShoppingCart();
    }

    // Getter method for shoppingCart
    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }
}

// ShoppingCart class to keep track of items
class ShoppingCart {
    private List<Item> items;

    public ShoppingCart() {
        this.items = new ArrayList<>();
    }

    public List<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        for (Item existingItem : items) {
            if (existingItem.getName().equals(item.getName())) {
                // If the item already exists, increment its quantity and return
                existingItem.setQuantity(existingItem.getQuantity() + 1);
                existingItem.setTotal();
                return;
            }
        }
        // If the item does not exist, add it to the shopping cart
        items.add(item);
    }

    public void subtractItem(Item item) {
        for (Item existingItem : items) {
            if (existingItem.getName().equals(item.getName())) {
                if (existingItem.getQuantity() <= 0) {
                    items.remove(existingItem);
                }
                // If the item exists, decrement its quantity and return
                existingItem.setQuantity(existingItem.getQuantity() - 1);
                existingItem.setTotal();
                return;
            }
        }
    }

    public void deleteItem(Item item) {
        for (Item existingItem : items) {
            if (existingItem.getName().equals(item.getName())) {
                    items.remove(existingItem);
            }
        }
    }
}

// Item class (example)
class Item {
    private final String name;
    private final int price;
    private int quantity;
    private int total;

    public Item(String name, int price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.total = price * quantity;
    }

    // Getter methods for name and price
    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() { return quantity; }

    public void setQuantity(int newQuantity) { quantity = newQuantity; }

    public int getTotal() { return total; }

    public void setTotal() { total = price * quantity; }
}