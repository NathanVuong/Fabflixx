function handleStarResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#single_star_table_body");

    // Concatenate the html tags with resultData jsonObject
    let rowHTML = "";
    rowHTML += "<tr>";
    //title
    rowHTML += "<th>" + resultData[0]["name"] + "</th>";
    //year
    rowHTML += "<th>" + resultData[0]["year"] + "</th>";
    //director
    rowHTML += "<th>" + resultData[0]["movies"] + "</th>";
    rowHTML += "</tr>";

    // Append the row created to the table body, which will refresh the page
    starTableBodyElement.append(rowHTML)
}


/*
Once this .js is loaded, following scripts will be executed by the browser
*/

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movieList", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});
