<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Database Viewer - Grant</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/css/viewer.css" />
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/css/icons/icons.css" />
</head>
<body>
	<div class="error-message">
		<div class="error-icon">
			<span class="material-icons">priority_high</span>
		</div>
		<div class="error-message-text">An error occured</div>
		<div class="close-icon">
			<span class="material-icons">close</span>
		</div>
	</div>
	<div class="container">
		<div class="top" id="view-top">
			<div class="header">
				<h1>Database Viewer</h1>
				<p>By: Grant</p>
			</div>
			<c:if test="${null ne sessionScope.isLoggedIn}">
				<div class="controls">
					<span class="material-icons" id="logout"> logout </span>
				</div>
			</c:if>
		</div>
		<div class="content">
			<div class="steps">
				<div class="step">
					<div class="custom-select-container" id="database-select">
						<div class="custom-select-option preview">Select a database</div>
						<div class="custom-select-options">
							<c:forEach items="${databases}" var="item" varStatus="loop">
								<div class="custom-select-option" data-value="${loop.index}">${item}</div>
							</c:forEach>
						</div>
					</div>
				</div>
				<div class="step hidden" style="margin-left: 20px">
					<div class="custom-select-container" id="table-select"></div>
				</div>
			</div>
			<div class="table-view">
				<table>
					<tbody>
						<tr class="table-headers">
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	<c:if test="${null eq sessionScope.isLoggedIn}">
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
					<form action="login" method="POST" novalidate>
						<input class="login-field" type="text" name="username"
							placeholder="Username" required /> <br /> <input
							class="login-field" type="password" name="password"
							placeholder="password" style="margin-top: 15px" required /> <br />
						<p id="login-error"></p>
						<button class="login-submit">Log In</button>
					</form>
				</div>
			</div>
		</div>
		<script
			src="<%=request.getContextPath()%>/resources/js/notLoggedIn.js"></script>
	</c:if>
	<script src="<%=request.getContextPath()%>/resources/js/viewer.js"></script>
</body>
</html>