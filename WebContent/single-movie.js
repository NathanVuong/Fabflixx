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
    let movieTitleElement = jQuery("#movie_title")
    let directorElement = jQuery("#director_name")
    let genresElement = jQuery("#genres_list")
    let starsElement = jQuery("#stars_list")
    let ratingElement = jQuery("#rating_name")
    // Concatenate the html tags with resultData jsonObject
    let titleHTML = "";
    let directorHTML = "";
    let genresHTML = "";
    let starsHTML = "";
    let ratingHTML = "";
    //title
    titleHTML += resultData[0]["movie_title"];
    //year
    titleHTML += " (" + resultData[0]["movie_year"] + ")";
    //director
    directorHTML += resultData[0]["movie_director"];
    //genres (can be a combined string or combine them here depending)


    let genre_count = Number(resultData[1]["genre_count"]);
    for (let i = 0; i < genre_count; i++) {
        if (genre_count - i > 1){
            genresHTML += resultData[1]["genre_" + i] + ", ";
        }
        genresHTML += resultData[1]["genre_" + i];
    }
    //stars (same as with genres)
    let star_count = Number(resultData[2]["star_count"]);
    for (let i = 0; i < star_count; i++) {
        starsHTML += '<a href="single-star.html?id=' +
            resultData[2]['star_id_' + i] + '">'
            + resultData[2]["star_" + i] +
            '</a>';
        if (star_count - i > 1) {
            starsHTML += ", ";
        }
    }


    //rating
    ratingHTML += resultData[0]["movie_rating"];


    // Append the row created to the table body, which will refresh the page
    movieTitleElement.append(titleHTML);
    directorElement.append(directorHTML);
    genresElement.append(genresHTML);
    starsElement.append(starsHTML);
    ratingElement.append(ratingHTML);
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
