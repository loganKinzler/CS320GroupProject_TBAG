var isMoniterOn;

window.addEventListener('load', () => {
    const historyBox = document.querySelector('.history-box');
    historyBox.scrollTop = historyBox.scrollHeight;
	
	const powerButton = document.getElementById("power_button");
	powerButton.addEventListener("click", clickPowerButton);

	document.querySelector('input[name="userInput"]').focus();
});

function clickPowerButton() {

	// get session info for power button
	isMoniterOn = sessionStorage.getItem("moniterOn");
	if (isMoniterOn == 'true') isMoniterOn = true;
	else isMoniterOn = false;

	if (isMoniterOn) {
		console.log("turning moniter off...");

		// turn off moniter
		this.classList.remove("turnOn_PowerButton");
		void this.offsetWidth;
		this.classList.add("turnOff_PowerButton");
	
	} else {
		console.log("turning moniter on...");

		// turn on moniter
		this.classList.remove("turnOff_PowerButton");
		void this.offsetWidth;
		this.classList.add("turnOn_PowerButton");
	}

	// get session info for power button
	sessionStorage.setItem("moniterOn", !isMoniterOn);
}