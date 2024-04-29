let payment_form = $("#payment_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handlePaymentResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);


    // If login succeeds, it will redirect the user to movie-list.html
    if (resultDataJson["status"] === "success") {
        // change back later
        window.location.replace("confirmation.html");
    } else {
        $("#payment_error_message").text("Invalid information. Re-enter payment information.");
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitPaymentForm(formSubmitEvent) {
    console.log("submit payment form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/payment", {
            method: "POST",
            data: payment_form.serialize(),
            success: handlePaymentResult
        }
    );
}

function handleCostUpdate(resultData){
    console.log(resultData["total_cost"])
    $("#total-cost").html("Total Cost: " + resultData["total_cost"]);
}

// Bind the submit action of the form to a handler function
payment_form.submit(submitPaymentForm);

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/payment",
    success: handleCostUpdate // Setting callback function to handle data returned successfully by the StarsServlet
});

