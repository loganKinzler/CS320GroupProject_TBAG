var isMoniterOn;

window.onload = function () {

    const powerButton = document.getElementById("power_button");
    powerButton.addEventListener("click", () => {

        // get session info for power button
        isMoniterOn = sessionStorage.getItem("moniterOn");
        if (isMoniterOn == 'true') isMoniterOn = true;
        else isMoniterOn = false;

        if (isMoniterOn) {
            console.log("turning moniter off...");

            // turn off moniter
            powerButton.classList.remove("turnOn_PowerButton");
            void powerButton.offsetWidth;
            powerButton.classList.add("turnOff_PowerButton");
        
        } else {
            console.log("turning moniter on...");

            // turn on moniter
            powerButton.classList.remove("turnOff_PowerButton");
            void powerButton.offsetWidth;
            powerButton.classList.add("turnOn_PowerButton");
        }

        // get session info for power button
        sessionStorage.setItem("moniterOn", !isMoniterOn);
    });
};
