function createButtons() {
    const container = document.getElementById('buttons-container');

    // Create buttons for letters A-Z
    for (let i = 65; i <= 90; i++) {
        const char = String.fromCharCode(i);
        createButton(container, char);
    }

    // Create buttons for numbers 1-9
    for (let i = 1; i <= 9; i++) {
        createButton(container, i);
    }

    // Create button for *
    createButton(container, '*');
}

// Function to create a button
function createButton(container, label) {
    const button = document.createElement('button');
    button.textContent = label;
    button.addEventListener('click', () => {
        console.log('Button clicked:', label);
        // You can add your functionality here
    });
    container.appendChild(button);
}

// Call the function to create buttons when the page loads
document.addEventListener('DOMContentLoaded', createButtons);