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


function handleBrowsingResult(resultData) {
    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#movie_list_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        //title
        rowHTML += "<th>" +
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] +
            '</a>' + "</th>";
        //year
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        //director
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        //genres (can be a combined string or combine them here depending)
        for (let j = 1; j <= 3; j++) {
            if (resultData[i]["movie_genre_" + j] == null) {
                rowHTML += "<th>" + "N/A" + "</th>";
            }
            else {
                rowHTML += "<th>" + resultData[i]["movie_genre_" + j] + "</th>";
            }
        }
        //stars (same as with genres)
        for (let j = 1; j <= 3; j++) {
            if (resultData[i]["movie_star_" + j] == null) {
                rowHTML += "<th>" + "N/A" + "</th>";
            }
            else {
                rowHTML += "<th>" +
                    '<a href="single-star.html?id=' + resultData[i]["movie_star_id_" + j] + '">'
                    + resultData[i]["movie_star_" + j] +
                    '</a>' + "</th>";
            }
        }
        //rating
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "</tr>";
        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}

// Get id from URL
let title = getParameterByName('title');
let genre = getParameterByName('genre');

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/browseMovie?title=" + title + "&genre=" + genre,
    success: (resultData) => handleBrowsingResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});
