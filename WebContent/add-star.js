let star_form = $("#star_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleAddStarResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);


    // If login succeeds, it will redirect the user to movie-list.html
    if (resultDataJson["status"] === "success") {
        // change back later
        $("#status_message").text("Successfully added star!");
    } else {
        $("#status_message").text("Unable to add star...");
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitStarForm(formSubmitEvent) {
    console.log("submit payment form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/addStar", {
            method: "POST",
            success: handleAddStarResult
        }
    );
}

// Bind the submit action of the form to a handler function
star_form.submit(submitStarForm);