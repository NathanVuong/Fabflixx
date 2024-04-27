function handleBrowsing(resultData) {
    const container = document.getElementById('buttons-container');
    // Create buttons for letters A-Z
    for (let i = 65; i <= 90; i++) {
        const char = String.fromCharCode(i);
        let link = "browse-result.html?" + "title=" + char + "&genre=";
        createButton(container, char, link);
    }

    // Create buttons for numbers 1-9
    for (let i = 1; i <= 9; i++) {
        const char = i.toString();
        let link = "browse-result.html?" + "title=" + char + "&genre=";
        createButton(container, i, link);
    }

    // Create button for *
    let link = "browse-result.html?title=*&genre=";
    createButton(container, '*', link);

    for (let i = 0; i < resultData.length; i++) {
        let genreLink = "browse-result.html?title=&genre=" + resultData[i];
        createButton(container, resultData[i], genreLink)
    }
}

// Function to create a button
function createButton(container, label, link) {
    const button = document.createElement('button');
    button.textContent = label;
    button.addEventListener('click', () => {
        console.log('Button clicked:', label);
        window.location.href = link;
    });
    container.appendChild(button);
}

// Call the function to create buttons when the page loads
// document.addEventListener('DOMContentLoaded', createButtons);

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/getGenres",
    success: (resultData) => handleBrowsing(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});