window.addEventListener('load', () => {
    const historyBox = document.querySelector('.history-box');
    historyBox.scrollTop = historyBox.scrollHeight;
	
	document.querySelector('input[name="userInput"]').focus();
});


function debugLoop() {
	requestAnimationFrame(debugLoop);
}
debugLoop();