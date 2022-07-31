<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>DbViewer | Log In</title>
<link rel="stylesheet" href="<c:url value="/resources/css/login.css"/>" />
<link rel="stylesheet" href="<c:url value="/resources/css/viewer.css"/>" />
<link rel="stylesheet" href="<c:url value="/resources/css/icons/icons.css"/>" />
</head>
<body>
	<div class="login-layer"
		style="position: fixed; top: 0; left: 0; height: 100%; width: 100%; display: flex; align-items: center; justify-content: center;">
		<div class="login-modal">
			<div class="top" style="width: 310px">
				<div class="header">
					<h1>Please Log In</h1>
					<p>The page you are trying to visit is protected</p>
				</div>
			</div>
			<div class="login-form">
				<div class="loading-layer">
					<span class="material-icons"> hourglass_top </span>
				</div>
				<form action="/login" method="POST" novalidate="novalidate" id="loginForm">
					<input class="login-field" type="text" name="username"
						placeholder="Username" required="required" /> <br /> 
					<input class="login-field" type="password" name="password"
						placeholder="Password" style="margin-top: 15px" required="required" /> <br />
					<sec:csrfInput/>
					<p id="login-error"></p>
					<button class="login-submit">Log In</button>
				</form>
			</div>
		</div>
	</div>
	<script src="<c:url value="/resources/js/login.js"/>"></script>
</body>
</html>