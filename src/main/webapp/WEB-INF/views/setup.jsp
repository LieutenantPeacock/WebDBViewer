<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>DbViewer | Set Up</title>
<link rel="stylesheet" href="<c:url value="/resources/css/icons/icons.css"/>" />
<style>
.setup-modal {
    width: 60%;
    height: 70%;
    max-width: 1000px;
    max-height: 1000px;
    background-color: white;
    box-shadow: 3px 3px black;
    border-left: 1px solid;
    border-top: 1px solid;
    font-family: 'Bebas Neue';
    display: flex;
}
.top {
    font-family: 'Bebas Neue';
    background-color: #485761;
    color: white;
}
.header {
    overflow: auto;
    margin: auto;
    width: 80%;
}
.header h1 {
    letter-spacing: 1px;
    margin-bottom: 0;
}
.header p {
    margin-top: 0px;
    font-size: 25px;
}
.setup-modal .setup-form {
    text-align: center;
    height: 100%;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
}
#setupForm label {
	display: block;
	font-size: 1.35em;
	font-weight: bold;
	margin-bottom: 5px;
}
#setupForm input {
	margin-bottom: 15px;
	width: 100%;
	padding: 5px;
}
#setupForm button {
	cursor: pointer;
	padding: 10px 20px;
	font-size: 1.3em;
	color: #FFF;
	border: none;
	border-radius: 5px;
	background-color: #34baeb;
}
#setupForm div.error {
	color: red;
	margin-bottom: 5px;
	font-size: 1.em;
}
#setupForm input.error {
	border: 1px solid red;
}
body {
	background-color: #d3d3d3;
}
</style>
</head>
<body>
	<div class="setup-layer"
		style="position: fixed; top: 0; left: 0; height: 100%; width: 100%; display: flex; align-items: center; justify-content: center;">
		<div class="setup-modal">
			<div class="top" style="width: 50%;">
				<div class="header">
					<h1>Welcome to DB Viewer!</h1>
					<p>
					Please set up the administrator user for this instance.
					You will need the generated temporary password from the logs of the application
					to verify that you have access to the server.
					</p>
				</div>
			</div>
			<div class="setup-form">
				<sf:form method="POST" novalidate="novalidate" id="setupForm" modelAttribute="setupForm">
					<sf:label path="tempPassword">Current (Temporary) Password</sf:label>
					<sf:input class="login-field" type="password" path="tempPassword"
						required="required" cssErrorClass="error"/> 
					<sf:errors path="tempPassword" cssClass="error" element="div"/>
					<sf:label path="username">Admin Account Username</sf:label>
					<sf:input class="login-field" type="text" path="username" 
						required="required" cssErrorClass="error" value="admin"/> 
					<sf:errors path="username" cssClass="error" element="div"/>
					<sf:label path="password">New Password</sf:label>
					<sf:input class="login-field" type="password" path="password"
						required="required" cssErrorClass="error"/> 
					<sf:errors path="password" cssClass="error" element="div"/>
					<sf:label path="confirmPassword">Confirm Password</sf:label>
					<sf:input class="login-field" type="password" path="confirmPassword"
						required="required" /> 
					<button>Set Up</button>
				</sf:form>
			</div>
		</div>
	</div>
</body>
</html>