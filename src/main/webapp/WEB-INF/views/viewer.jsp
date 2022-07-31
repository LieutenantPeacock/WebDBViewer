<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Database Viewer - Grant</title>
<link rel="stylesheet" href="<c:url value="/resources/css/viewer.css"/>" />
<link rel="stylesheet" href="<c:url value="/resources/css/icons/icons.css"/>" />
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
			<sec:authorize access="isAuthenticated()">
				<form method="POST" action="<c:url value="/logout"/>">
					<div class="controls">
						<button style="background: transparent; border: none; padding: 0;">
							<span class="material-icons" id="logout"> logout </span>
						</button>
					</div>
					<sec:csrfInput></sec:csrfInput>
				</form>
			</sec:authorize>
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
	<script src="<c:url value="/resources/js/viewer.js"/>"></script>
</body>
</html>