function handleCartResult(resultData) {
    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#checkout_list_table_body");

    starTableBodyElement.empty();

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < resultData.length; i++) {
        // Concatenate the html tags with resultData jsonObject
        let currentItem = resultData[i];

        // Create a new row for the table
        let rowHTML = "<tr>";

        // Add the item title, price, quantity, and total to the row
        rowHTML += "<td>" + currentItem["Title"] + "</td>"; // Item title
        rowHTML += "<td>$" + currentItem["Price"] + "</td>";
        rowHTML += "<td><button onclick=\"modifyItem('" + currentItem["Title"] + "', -" + currentItem["Price"] + ", '" + currentItem["MovieId"] + "')\">-</button>" +
            currentItem["Quantity"] + "<button onclick=\"modifyItem('" + currentItem["Title"] + "', " + currentItem["Price"] + ", '" + currentItem["MovieId"] + "')\">+</button></td>";
        rowHTML += "<td>$" + currentItem["Total"] + "</td>";
        rowHTML += "<td><button onclick=\"modifyItem('" + currentItem["Title"] + "', 'delete'" + ", '" + currentItem["MovieId"] + "')\">Delete</button></td>";

        // Close the table row
        rowHTML += "</tr>";

        starTableBodyElement.append(rowHTML);
    }
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/shoppingCart",
    success: (resultData) => handleCartResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

function getCheckout() {
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/shoppingCart",
        success: (resultData) => handleCartResult(resultData)
    });
}

function modifyItem(title, price, movieId) {
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "POST", // Setting request method
        url: "api/shoppingCart?title=" + title + "&price=" + price + "&movieId" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
        cache: false,
        success: function() {
            // Call getCheckout to update the page after the modification request is successful
            getCheckout();
        }
    });
}