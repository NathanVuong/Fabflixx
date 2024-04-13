function getParameterByName(target){
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let movieTableBodyElement = jQuery("#single_movie_table_body");
    // Concatenate the html tags with resultData jsonObject
    let rowHTML = "";
    rowHTML += "<tr>";
    //title
    rowHTML += "<th>" + resultData[0]["movie_title"] + "</th>";
    //year
    rowHTML += "<th>" + resultData[0]["movie_year"] + "</th>";
    //director
    rowHTML += "<th>" + resultData[0]["movie_director"] + "</th>";
    //genres (can be a combined string or combine them here depending)

    let genre_count = Number(resultData[1]["genre_count"]);
    for (let i = 0; i < genre_count; i++) {
        rowHTML += "<th>" + resultData[1]["genre_" + i] + "</th>";
    }
    //stars (same as with genres)
    let star_count = Number(resultData[2]["star_count"]);
    for (let i = 0; i < star_count; i++) {
        rowHTML += "<th>" + '<a href="single-star.html?id=' +
            resultData[2]['star_id_' + i] + '">'
            + resultData[2]["star_" + i] +
            '</a>' + "</th>";
    }

    //rating
    rowHTML += "<th>" + resultData[0]["movie_rating"] + "</th>";
    rowHTML += "</tr>";

    // Append the row created to the table body, which will refresh the page
    movieTableBodyElement.append(rowHTML);
}


/*
Once this .js is loaded, following scripts will be executed by the browser
*/

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});