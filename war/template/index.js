const console_input = document.getElementById("console-input");
const console_output = document.getElementById("output")
let inputs = []

console_input.addEventListener("keydown", function(e) {
	if (e.key === "Enter") {
		e.preventDefault(); //Makes sure that the user is still typing in the text input field after they press enter
		
		let input = console_input.value;
		input = input.replace(/\s/g, ""); //Removes spaces (if it turns out users will be putting spaces in commands, we can just use trim())
		
		//Log inputs (not persistent)
		if (input != "") {
			//Store typed text and clear input field
			inputs.push(input);
			
			//Code needed to add to console history
			let newOutput = document.createElement("p");
			newOutput.textContent = input;
			newOutput.id = "AAA"
			console_output.appendChild(newOutput);
		}

		console_input.value = "";
		
		
		
		
		console.log(document.getElementById("AAA").textContent);
		Array.from(console_output.children).forEach(child => console.log(child)); //Debug
		
		
	}
});

document.addEventListener("DOMContentLoaded", function() {
	console_input.focus();
});