function handleCartResult(resultData) {
    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#checkout_list_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < resultData.length; i++) {
        // Concatenate the html tags with resultData jsonObject
        let currentItem = resultData[i];

        // Create a new row for the table
        let rowHTML = "<tr>";

        // Add the item title, price, quantity, and total to the row
        rowHTML += "<td>" + currentItem["Title"] + "</td>"; // Item title
        rowHTML += "<td>$" + currentItem["Price"] + "</td>";
        rowHTML += "<td>" + currentItem["Quantity"] + "</td>";
        rowHTML += "<td>$" + currentItem["Total"] + "</td>";

        // Close the table row
        rowHTML += "</tr>";

        starTableBodyElement.append(rowHTML);
    }
}

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/shoppingCart",
    success: (resultData) => handleCartResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});
