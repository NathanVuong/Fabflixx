let searchForm= $("#search-submit");

function handleSearchResults(searchEvent) {

    searchEvent.preventDefault();

    $.ajax("api/searchBrowseMovie", {
        method: "POST",
        data: searchForm.serialize(),
        success: (resultData) => displaySearchResults(resultData)
    });
}

function displaySearchResults(resultData) {
    let resultDataJson = JSON.parse(resultData);
    scope.searchDetails = resultDataJson;
    window.location.replace("search-result.html");

}

function handleMovieListResult() {
    resultData = window.scope.searchDetails;

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
        rowHTML += "<th>" + resultData[i]["movie_genre_1"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_genre_2"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_genre_3"] + "</th>";
        //stars (same as with genres)
        rowHTML += "<th>" +
            '<a href="single-star.html?id=' + resultData[i]['movie_star_1'] + '">'
            + resultData[i]["movie_star_id_1"] +
            '</a>' + "</th>";
        rowHTML += "<th>" +
            '<a href="single-star.html?id=' + resultData[i]['movie_star_2'] + '">'
            + resultData[i]["movie_star_id_2"] +
            '</a>' + "</th>";
        rowHTML += "<th>" +
            '<a href="single-star.html?id=' + resultData[i]['movie_star_3'] + '">'
            + resultData[i]["movie_star_id_3"] +
            '</a>' + "</th>";
        //rating
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "</tr>";
        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}

searchForm.submit(handleSearchResults);