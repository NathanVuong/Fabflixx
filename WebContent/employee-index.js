function handleMetadataResult(resultData) {
    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let metadataTableBodyElement = jQuery("#metadata_table_body");


    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < resultData.length; i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        //title
        rowHTML += "<th>" + resultData[i]["table_name"] + "</th>";
        //year
        rowHTML += "<th>" + resultData[i]["attribute_name"] + "</th>";
        //director
        rowHTML += "<th>" + resultData[i]["attribute_type"] + "</th>";
        rowHTML += "</tr>";
        // Append the row created to the table body, which will refresh the page
        metadataTableBodyElement.append(rowHTML);
    }
}

/*
Once this .js is loaded, following scripts will be executed by the browser
*/

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/metadataServlet", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMetadataResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});