var crtIsOn = false;
var clickTime = new Date().getSeconds() - 1;
var firstClick = true;

window.addEventListener('DOMContentLoaded', () => {
    const crtContent = document.getElementById("crt_content");
	
    const powerButton = document.getElementById("power_button");
    powerButton.addEventListener("click", clickPowerButton.bind(powerButton, crtContent));
});

function clickPowerButton(over_div) {
    const currentTime = new Date().getSeconds();
	
	const deltaClickTime = currentTime - clickTime;
    if (deltaClickTime <= 1 && deltaClickTime >= 0) return;
    clickTime = currentTime;
    
    if (!crtIsOn) {
        over_div.classList.remove("crt_off");
        over_div.classList.add("crt_open");
		
		if (firstClick) firstClick = false;	
		else void over_div.offsetWidth;      
        
		setTimeout(() => {
	   		over_div.classList.remove("crt_open");
        }, 1500);
        
    } else {
        over_div.classList.add("crt_close");
		void over_div.offsetWidth;

        setTimeout(() => {
            over_div.classList.remove("crt_close");
            over_div.classList.add("crt_off");
	    void over_div.offsetWidth;
        }, 1500);
    }
    
    crtIsOn = !crtIsOn;
}