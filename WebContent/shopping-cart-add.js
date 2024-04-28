function addItem(title, price) {
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "POST", // Setting request method
        url: "api/shoppingCart?title=" + title + "&price=" + price, // Setting request url, which is mapped by StarsServlet in Stars.java
    });
}