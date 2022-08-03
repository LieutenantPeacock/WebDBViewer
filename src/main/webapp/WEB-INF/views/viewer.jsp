<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="_csrf" content="${_csrf.token}" />
<meta name="_csrf_header" content="${_csrf.headerName}" />
<title>Database Viewer - Grant</title>
<link rel="stylesheet"
	href="<c:url value="/resources/css/style.css"/>" />
<link rel="stylesheet"
	href="<c:url value="/resources/css/icons/icons.css"/>" />
<link rel="stylesheet"
	href="<c:url value="/resources/css/bootstrap-5.2.0.min.css"/>" />
<style>
@import
	url('https://fonts.googleapis.com/css?family=Bebas+Neue&display=swap');

#header {
	background-color: #485761;
	color: #fff;
	font-family: 'Bebas Neue';
	position: relative;
	padding: 5px 10%;
}

#header h1 {
	margin-bottom: 0;
}

#header p {
	font-size: 25px;
	margin-top: 0;
	font-style: italic;
}

#header a {
	color: inherit;
	text-decoration: none;
}

#header a:hover {
	text-decoration: underline;
	cursor: pointer;
}

#logoutControl {
	position: absolute;
	top: 0;
	right: 0;
	font-size: 1.2em;
}

#logoutControl .material-icons {
	padding: 10px;
	background-color: white;
	color: black;
}

#settingsCol {
	width: 30%;
	border: 1px solid black;
	flex-shrink: 0;
	flex-grow: 0;
	flex-basis: 30%;
	max-width: 300px;
	overflow-y: auto;
}

html, body {
	height: 100%;
}

#connectionsContainer > .card.active {
	background-color: #0cf74b;
}

#tableSelectContainer {
	width: 30%;
	padding: 5px;
}
</style>
</head>
<body class="d-flex flex-column">
	<input type="hidden" id="basePath" value="<c:url value="/"/>"/>
	<div id="header">
		<h1><a href="<c:url value="/"/>">DB Viewer</a></h1>
		<p>Lieutenant Peacock</p>
		<sec:authorize access="isAuthenticated()">
			<form method="POST" action="<c:url value="/logout"/>" title="Log Out">
				<div id="logoutControl">
					<button style="background: transparent; border: none; padding: 0;">
						<span class="material-icons"> logout </span>
					</button>
				</div>
				<sec:csrfInput />
			</form>
		</sec:authorize>
	</div>
	<div class="container-fluid flex-fill">
		<div class="row h-100">
			<div class="col" id="settingsCol">
				<div id="connectionsContainer">
					<h2 class="text-primary">Connections</h2>
					<c:if test="${empty connections}">
						<span style="font-weight: bold;" id="noConnections">Currently no connections!</span>
					</c:if>
					<sec:authorize access="hasRole('ADMIN')">
						<button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#newConnectionModal">
							Add New Connection
						</button>
					</sec:authorize>
					<c:forEach items="${connections}" var="connection">
						<div class="card mt-3 ${connection.id == param.connection ? 'active' : ''}">
						  <div class="card-body">
						    <h5 class="card-title">${connection.url} with username [${connection.username}]</h5>
						    <p class="card-text">
						    	<strong class="text-decoration-underline">Driver Path</strong>: ${connection.driverPath} <br/>
						    	<strong class="text-decoration-underline">Driver Class Name</strong>: ${connection.driverName}
						    </p>
						    <a href="<c:url value="/?connection=${connection.id}"/>" class="btn btn-primary">Open Connection</a>
						  </div>
						</div>
					</c:forEach>
				</div>
				<template id="connectionTemplate">
					<div class="card mt-3">
					  <div class="card-body">
					    <h5 class="card-title">${connection.url} with username [${connection.username}]</h5>
					    <p class="card-text">
					    	<strong class="text-decoration-underline">Driver Path</strong>: ${connection.driverPath} <br/>
					    	<strong class="text-decoration-underline">Driver Class Name</strong>: ${connection.driverName}
					    </p>
					    <a href="<c:url value="/?connection=${connection.id}"/>" class="btn btn-primary">Open Connection</a>
					  </div>
					</div>
				</template>
			</div>
			<div class="col" style="position: relative;">
				<c:if test="${tables != null}">
					<div id="tableSelectContainer" class="d-flex align-items-center">
						<span class="h4">Table:</span>
						<select class="form-select" id="tableSelect" style="margin-left: 5px;">
							<c:forEach items="${tables}" var="table">
								<option value="${table}">${table}</option>
							</c:forEach>
						</select>
					</div>
				</c:if>
			</div>
		</div>
	</div>
	<sec:authorize access="hasRole('ADMIN')">
	<div class="modal fade" id="newConnectionModal" tabindex="-1"
		aria-labelledby="exampleModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-dialog-scrollable">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="exampleModalLabel">New Connection</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<form class="needs-validation" novalidate id="newConnectionForm">
					  <div class="mb-3">
					    <label for="connection_url" class="form-label">JDBC URL</label>
					    <input type="text" class="form-control" id="connection_url" name="url" required/>
					    <div class="invalid-feedback">Please enter the JDBC URL.</div>
					  </div>
					  <div class="mb-3">
					    <label for="connection_username" class="form-label">Username</label>
					    <input type="text" class="form-control" id="connection_username" name="username"/>
					  	<div class="invalid-feedback"></div>
					  </div>
					  <div class="mb-3">
					    <label for="connection_password" class="form-label">Password</label>
					    <input type="password" class="form-control" id="connection_password" name="password"/>
					    <div class="invalid-feedback"></div>
					  </div>
					  <hr/>
					  <div>
					  	<label for="connection_driverPath" class="form-label">Select JDBC Driver Location</label>
					  	<select class="form-select" id="connection_driverPath" name="driverPath" required>
					  		<c:forEach var="driver" items="${requestScope.drivers}">
					  			<option value="${driver}">${driver}</option>
					  		</c:forEach>
					  	</select>
					  	<div class="invalid-feedback"></div>
					  	<div class="invalid-feedback" id="driverPathError">Please select the JDBC Driver Location.</div>
					  	<div id="driverUploadContainer">
					  	<label for="driverUpload" class="form-label">Or Upload New Driver (one or more JAR files)</label>
					  	<input class="form-control no-auto-validate" type="file" id="driverUpload" multiple accept=".jar"/>
					  	<div class="invalid-feedback" id="driverUploadError"></div>
					  	<label for="driverFolderName" class="form-label">Driver Folder Name (required if more than one JAR file)</label>
					  	<input class="form-control no-auto-validate" type="text" id="driverFolderName"/>
					  	<div class="invalid-feedback" id="driverFolderError"></div>
					  	<button type="button" class="btn btn-info mt-2 w-100" id="driverUploadBtn">Upload</button>
					  	</div>
					  </div>
					  <div id="driverNameContainer" style="display: none;">
					    <hr class="mt-3"/>
					  	<label for="connection_driverName" class="form-label">Driver Name</label>
					  	<select class="form-select" id="connection_driverName" name="driverName" required>
					  	</select>
					  	<div class="invalid-feedback"></div>
					  </div>
					  <div class="mt-3 text-danger" id="connectionFormError"></div>
					 </form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal">Close</button>
					<button type="submit" class="btn btn-outline-success" form="newConnectionForm">Save</button>
				</div>
			</div>
		</div>
	</div>
	</sec:authorize>
	<script src="<c:url value="/resources/js/bootstrap-5.2.0.bundle.min.js"/>"></script>
	<script src="<c:url value="/resources/js/viewer.js"/>"></script>
</body>
</html>