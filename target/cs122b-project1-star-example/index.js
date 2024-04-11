function handleStarResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#movie_list_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        //title
        rowHTML += "<th>" + resultData[i]["movie_title"] + "</th>";
        //year
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        //director
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        //genres (can be a combined string or combine them here depending)
        rowHTML += "<th>" + resultData[i]["movie_genre_1"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_genre_2"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_genre_3"] + "</th>";
        //stars (same as with genres)
        rowHTML += "<th>" + resultData[i]["movie_star_1"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_star_2"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_star_3"] + "</th>";
        //rating
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
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
