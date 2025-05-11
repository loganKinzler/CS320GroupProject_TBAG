window.addEventListener('DOMContentLoaded', () => {
    const historyBox = document.querySelector('.history-box');
    historyBox.scrollTop = historyBox.scrollHeight;
	
	document.querySelector('input[name="userInput"]').focus();
});

function clickPowerButton() {
	const crtContent = document.getElementById("crt_content");
    crtContent.classList.add("crt_close");

    setTimeout(() => {
		crtContent.classList.remove("crt_close");
		crtContent.classList.add("crt_off");	
		window.location.href = '../index';
    }, 1500);
}