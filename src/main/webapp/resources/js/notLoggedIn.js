/**
 * 
 */
document.querySelector('.container').style.filter = "blur(2px)";
const loginForm = document.querySelector('form[action="login"]');
const errorMessage = document.getElementById("login-error");
const loadingLayer = document.querySelector('.loading-layer');
loginForm.addEventListener('submit', function(e) {
	e.preventDefault();
	const username = loginForm.username;
	if (username.value.length < 1) {
		errorMessage.textContent = "Please enter a username";
	} else {
		const password = loginForm.password;
		if (password.value.length < 1) {
			errorMessage.textContent = "Please enter a password";
		} else {
			sendAuthenticationRequest(username.value, password.value);
		}
	}
})
function sendAuthenticationRequest(username, password) {
	loadingLayer.style.display = "flex";
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			const response = this.responseText;
			setTimeout(function() {
				const JSONResponse = JSON.parse(response);
				if (JSONResponse.success == true) {
					loadingLayer.style.display = "none";
					location = location;
				} else {
					errorMessage.textContent = JSONResponse.message;
				}
			}, 1000)
		}
		;
	}
	xhttp.open("POST", "/login?username=" + encodeURIComponent(username) + "&password=" + encodeURIComponent(password),
			true);
	xhttp.send();
}