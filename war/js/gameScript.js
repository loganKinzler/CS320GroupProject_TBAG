var crtIsOn = true;
var clickTime = new Date().getSeconds() - 1;

window.addEventListener('DOMContentLoaded', () => {
    const historyBox = document.querySelector('.history-box');
    historyBox.scrollTop = historyBox.scrollHeight;
	
	document.querySelector('input[name="userInput"]').focus();
});

function clickPowerButton() {
    const currentTime = new Date().getSeconds();
	const deltaClickTime = currentTime - clickTime;
	
    if (deltaClickTime <= 1 && deltaClickTime >= 0) return;
    clickTime = currentTime;

	const crtContent = document.getElementById("crt_content");
    
    if (!crtIsOn) {
        crtContent.classList.remove("crt_off");
		void crtContent.offsetWidth;
        crtContent.classList.add("crt_open");
		void crtContent.offsetWidth;	
        
		setTimeout(() => {
	   		crtContent.classList.remove("crt_open");
			void crtContent.offsetWidth;
        }, 1500);
        
    } else {
        crtContent.classList.add("crt_close");
		void crtContent.offsetWidth;
		
        setTimeout(() => {
            crtContent.classList.remove("crt_close");
			void crtContent.offsetWidth;
			crtContent.classList.add("crt_off");
	    	void crtContent.offsetWidth;
        }, 1500);
    }
    
    crtIsOn = !crtIsOn;
}