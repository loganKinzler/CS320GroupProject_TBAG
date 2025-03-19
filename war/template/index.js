// Sample history data (replace this with data fetched from the server)
let history = [];

// Function to add a new history item
function addHistoryItem(item) {
  history.push(item); // Add the item to the history array
  renderHistory(); // Update the display
}

// Function to render the history items
function renderHistory() {
  const historyItemsContainer = document.getElementById("history-items");
  historyItemsContainer.innerHTML = ""; // Clear the container

  history.forEach((item) => {
    const itemElement = document.createElement("div");
    itemElement.className = "history-item";
    itemElement.textContent = item;
    historyItemsContainer.appendChild(itemElement);
  });
}

// Handle form submission
document.getElementById("input-form").addEventListener("submit", (event) => {
  event.preventDefault(); // Prevent the form from submitting the traditional way

  const userInput = document.getElementById("user-input").value;
  if (userInput.trim() === "") return; // Ignore empty input

  // Add the user input to the history
  addHistoryItem(userInput);

  // Clear the input field
  document.getElementById("user-input").value = "";

  // Send the input to the server (you'll need to implement this part)
  sendInputToServer(userInput);
});

// Function to send user input to the server
function sendInputToServer(input) {
  // Replace this with your actual server communication logic
  fetch("/game", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ userInput: input }),
  })
    .then((response) => response.json())
    .then((data) => {
      // Handle the server response (if needed)
      console.log("Server response:", data);
    })
    .catch((error) => {
      console.error("Error:", error);
    });
}

// Initial render of history (if there's any preloaded data)
renderHistory();