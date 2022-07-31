/**
 * 
 */
const loginForm = document.querySelector('form#loginForm');
const errorMessage = document.getElementById("login-error");
const loadingLayer = document.querySelector('.loading-layer');
loginForm.addEventListener('submit', function(e) {
	errorMessage.textContent = '';
	if (!this.username.value) {
		errorMessage.textContent = "Please enter a username";
		e.preventDefault();
	} else if (!this.password.value){
		errorMessage.textContent = "Please enter a password";
		e.preventDefault();
	}
});