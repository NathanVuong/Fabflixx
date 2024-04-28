function handleResult(resultData) {
    let resultElement = jQuery("#result-nav");
    let result_url = resultData[0]['result'];
    resultElement.prop('href', result_url);
}

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/result", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});
