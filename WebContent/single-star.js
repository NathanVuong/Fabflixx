function getParameterByName(target) {
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


function handleStarResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");


    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let moviesElement = jQuery("#movies_table_body");
    let starElement = jQuery("#star_name");


    // Concatenate the html tags with resultData jsonObject
    let rowHTML = "";
    let starHTML = "";
    // name and year of birth
    starHTML +=  resultData[0]["star_name"] + " (" + resultData[0]["star_birth_year"] + ")";
    let movie_count = Number(resultData[1]["movie_count"]);
    for (let i = 0; i < movie_count; i++) {
        rowHTML += "<tr>";
        //name
        rowHTML += "<th>" + '<a href="single-movie.html?id=' +
            resultData[1]['movie_id_' + i] + '">'
            + resultData[1]["movie_" + i] +
            '</a>' + "</th>";
        //year of birth
        rowHTML += "<th>" + resultData[1]["movie_year_" + i] + "</th>";
        //director
        rowHTML += "<th>" + resultData[1]["movie_director_" + i] + "</th>";
    }


    // Append the row created to the table body, which will refresh the page
    starElement.append(starHTML);
    moviesElement.append(rowHTML);
}




/*
Once this .js is loaded, following scripts will be executed by the browser
*/


// Get id from URL
let starId = getParameterByName('id');


// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/single-star?id=" + starId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});
