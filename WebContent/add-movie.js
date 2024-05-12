let movie_form = $("#movie_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleAddMovieResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);


    // If login succeeds, it will redirect the user to movie-list.html
    if (resultDataJson["status"] === "success") {
        // change back later
        $("#status_message").text("Successfully added movie! Movie ID: " + resultDataJson["movieId"] + " Genre ID: " + resultDataJson["genreId"] + " Star ID: " + resultDataJson["starId"]);
    } else {
        $("#status_message").text("Unable to add movie... Movie already exists?");
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitMovieForm(formSubmitEvent) {
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/addMovie", {
            method: "POST",
            success: handleAddMovieResult
        }
    );
}

// Bind the submit action of the form to a handler function
movie_form.submit(submitMovieForm);