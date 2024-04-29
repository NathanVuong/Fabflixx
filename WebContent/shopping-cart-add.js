function addItem(title, price, movieId) {
    console.log(title + " " + price + " " + movieId);
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "POST", // Setting request method
        url: "api/shoppingCart?title=" + title + "&price=" + price + "&movieId" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    });
    alert("Added item to cart!");
}