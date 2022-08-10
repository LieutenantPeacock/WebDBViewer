<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="lp" uri="http://lt-peacock.com/tags"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="_csrf" content="${_csrf.token}" />
<meta name="_csrf_header" content="${_csrf.headerName}" />
<title>Database Viewer - Grant</title>
<link rel="stylesheet"
	href="<c:url value="/resources/css/icons/icons.css"/>" />
<link rel="stylesheet"
	href="<c:url value="/resources/css/bootstrap-5.2.0.min.css"/>" />
<link rel="stylesheet" href="<c:url value="/resources/css/jquery-ui-1.13.2.min.css"/>"/>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.2/css/all.min.css" integrity="sha512-1sCRPdkRXhBV2PBLUdRb4tMg1w2YPf37qatUFeS7zlBy7jJI8Lf4VHwWfZZfpXtYSLy85pkm9GaYVYMfw5BC1A==" crossorigin="anonymous" referrerpolicy="no-referrer" />
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
	min-width: 200px;
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
	padding: 5px;
}

#tableContents td {
	word-break: break-all; /* overflow-wrap: break-word; */
}

.connection-edit {
	font-size: inherit;
	color: green;
	cursor: pointer;
}

.remove-connection-user {
	vertical-align: middle;
	color: red;
	cursor: pointer;
}


.sort-icon {
	cursor: pointer;
}
</style>
</head>
<body>
	<input type="hidden" id="basePath" value="<c:url value="/"/>"/>
	<div class="container-fluid h-100 d-flex flex-column">
		<div id="header" class="row flex-shrink-0">
			<h1><a href="<c:url value="/"/>">DB Viewer</a></h1>
			<p>Lieutenant Peacock</p>
			<sec:authorize access="isAuthenticated()">
				<sec:authentication property="principal.id" var="userId"/>
				<input type="hidden" id="userId" value="${userId}"/>
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
		<div class="row flex-grow-1" style="min-height: 0;">
			<div class="col border mh-100" id="settingsCol">
				<div id="connectionsContainer" class="pb-3">
					<h2 class="text-primary">Connections</h2>
					<c:if test="${empty connections}">
						<span style="font-weight: bold;" id="noConnections">Currently no connections!</span>
					</c:if>
					<sec:authorize access="hasRole('ADMIN')">
						<button type="button" class="btn btn-primary" id="newConnectionBtn" data-bs-toggle="modal" data-bs-target="#connectionModal">
							Add New Connection
						</button>
					</sec:authorize>
					<c:forEach items="${connections}" var="connection">
						<div class="card mt-3 connection-container ${connection.id == param.connection ? 'active' : ''}" data-id="${connection.id}">
						  <div class="card-body">
						    <h5 class="card-title"><span class="connection-name">${connection.name}</span>
						    	<sec:authorize access="hasRole('ADMIN')">
						    		<span class="material-icons connection-edit" title="Edit">edit</span>
						    	</sec:authorize>
						    </h5>
					    	<details class="card-text">
					    		<summary>Details</summary>
					    		<strong class="text-decoration-underline">JDBC URL</strong>: ${connection.url} <br/>
					    		<strong class="text-decoration-underline">Username</strong>: ${connection.username} <br/>
					    		<strong class="text-decoration-underline">Driver Path</strong>: ${connection.driverPath} <br/>
						    	<strong class="text-decoration-underline">Driver Class Name</strong>: ${connection.driverName}
					    	</details>
						    <a href="<c:url value="/?connection=${connection.id}"/>" class="btn btn-primary">Open Connection</a>
						  </div>
						</div>
					</c:forEach>
				</div>
				<template id="connectionTemplate">
					<div class="card mt-3 connection-container">
					  <div class="card-body">
					    <h5 class="card-title"><span class="connection-name">${connection.name}</span>
					    	<sec:authorize access="hasRole('ADMIN')">
					    		<span class="material-icons connection-edit" title="Edit">edit</span>
					    	</sec:authorize>
					    </h5>
				    	<details class="card-text">
				    		<summary>Details</summary>
				    		<strong class="text-decoration-underline">JDBC URL</strong>: ${connection.url} <br/>
				    		<strong class="text-decoration-underline">Username</strong>: ${connection.username} <br/>
				    		<strong class="text-decoration-underline">Driver Path</strong>: ${connection.driverPath} <br/>
					    	<strong class="text-decoration-underline">Driver Class Name</strong>: ${connection.driverName}
				    	</details>
					    <a href="<c:url value="/?connection=${connection.id}"/>" class="btn btn-primary">Open Connection</a>
					  </div>
					</div>
				</template>
				<sec:authorize access="hasRole('ADMIN')">
					<div id="usersContainer" class="pb-3">
						<h2>Users</h2>
						<button type="button" class="btn btn-outline-dark" data-bs-toggle="modal" data-bs-target="#newUserModal">Create User</button>
						<ul class="list-group mt-2">
							<c:forEach var="user" items="${users}">
								<li class="list-group-item justify-content-between align-items-center">
									${user.username}
									<c:if test="${user.id == userId}">
										<span class="text-info fw-bold fst-italic">(You)</span>
									</c:if>
								</li>
							</c:forEach>
						</ul>
					</div>
				</sec:authorize>
			</div>
			<div class="col border mh-100" style="overflow: auto;">
				<c:if test="${tables != null}">
					<div id="tableSelectContainer" class="row">
						<div class="col-auto" style="min-width: 150px;">
							<div class="form-floating">
								<select class="form-select" id="tableSelect">
									<c:forEach items="${tables}" var="table">
										<option value="${table}" ${table == param.table ? 'selected': ''}>${table}</option>
									</c:forEach>
								</select>
								<label for="tableSelect">Table</label>
							</div>
						</div>
						<div class="col">
							<form id="statementForm">
								<div class="input-group mb-3">
								  <div class="form-floating">
								      <textarea class="form-control" id="statementTextarea" 
								      	name="statement" placeholder="select * from ${param.table};"></textarea>
								      <label for="statementTextarea">Enter SQL Statement</label>
								  </div>
								  <button class="btn btn-outline-secondary">Execute</button>
								</div>
							</form>
						</div>
					</div>
				</c:if>
				<div class="text-center ${tableContentsError != null ? 'text-danger' : ''}" id="statementMessage">
					${tableContentsError}
				</div>
				<c:if test="${tableContents != null or tableContentsError != null}">
					<form id="whereFilterForm">
						<div class="input-group mb-3 mx-auto" style="width: 80%;">
							<span class="input-group-text">where</span>
							<input type="text" class="form-control" placeholder="SQL expression to filter results" value="${param.where}"/>
							<button class="btn btn-secondary">Apply Filter</button>
						</div>
					</form>
				</c:if>
				<table class="table table-bordered table-hover" id="tableContents">
					<c:if test="${tableContents != null}">
						<thead>
							<tr>
								<c:forEach var="column" items="${tableContents.columns}">
									<c:set var="sortClasses" value="${['fa-sort', 'fa-arrow-up-short-wide', 'fa-arrow-down-wide-short']}"/>
									<th scope="col">
										<span title="${column.name}: ${column.typeName}(${column.displaySize})">${column.name}</span>
										<i class="fa-solid ${sortClasses[column.dir.ordinal()]} sort-icon" title="Sort on column" data-sort-idx="${column.dir.ordinal()}"></i>
									</th>
								</c:forEach>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="row" items="${tableContents.rows}">
								<tr>
									<c:forEach var="val" items="${row}">
										<td>${val}</td>
									</c:forEach>
								</tr>
							</c:forEach>
						</tbody>
					</c:if>
				</table>
				<c:if test="${tableContents != null}">
					<div id="tablePagination">
						<lp:pagination currentPage="${currentPage}" firstPage="1" lastPage="${lastPage}"
							rootClass="pagination justify-content-center" currentClass="active"
							pageClass="page-item" generateDisabledLinks="true"
							pageLinkGenerator="${pageLinkGenerator}" basePageLink="${currentUri}"/>
					</div>
				</c:if>
			</div>
		</div>
	</div>
	<sec:authorize access="hasRole('ADMIN')">
	<div class="modal fade" id="connectionModal" tabindex="-1"
		aria-labelledby="connectionModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-dialog-scrollable">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="connectionModalLabel">New Connection</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<form class="needs-validation" novalidate id="connectionForm">
						<input type="hidden" name="id"/>
					  <div class="mb-3">
					  	<label for="connection_name" class="form-label">Connection Name</label>
					  	<input type="text" class="form-control" id="connection_name" name="name" required/>
					  	<div class="invalid-feedback">Please enter a name for the new connection.</div>
					  </div>
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
					  	<div class="invalid-feedback" id="driverPathError">Please select the JDBC Driver Location.</div>
					  	<div id="driverUploadContainer">
					  	<label for="driverUpload" class="form-label">Or Upload New Driver (one or more JAR files)</label>
					  	<input class="form-control" type="file" id="driverUpload" multiple accept=".jar"/>
					  	<div class="invalid-feedback" id="driverUploadError"></div>
					  	<label for="driverFolderName" class="form-label">Driver Folder Name (required if more than one JAR file)</label>
					  	<input class="form-control" type="text" id="driverFolderName"/>
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
					<div id="connectionUsers" class="pb-3" style="display: none;">
						<h3>Users</h3>
						<div id="noConnectionUsers" style="display: none;">No users!</div>
						<ul class="list-group mt-2">
						</ul>
						<form id="connectionAddUserForm">
							<div class="input-group mt-3">
								<div class="form-floating">
									<input type="text" class="form-control" id="connectionAddUser" placeholder="Username"/>
									<label for="connectionAddUser">Add User To Connection</label>
								</div>
								<button class="btn btn-outline-primary">Add</button>
							</div>
						</form>
						<div id="connectionAddUserError" class="text-danger"></div>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal">Close</button>
					<button type="submit" class="btn btn-outline-success" form="connectionForm">Save</button>
				</div>
			</div>
		</div>
	</div>
	<div class="modal fade" id="newUserModal" tabindex="-1"
		aria-labelledby="newUserModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-dialog-scrollable">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="newUserModalLabel">New User</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<form novalidate id="newUserForm" class="needs-validation">
						<div class="mb-3">
							<label for="newUser_username" class="form-label">Username</label>
							<input type="text" class="form-control" id="newUser_username" name="username" required/>
							<div class="invalid-feedback">Please enter a username.</div>
						</div>
						<div class="mb-3">
							<label for="newUser_password" class="form-label">Password</label>
							<input type="password" class="form-control" id="newUser_password" name="password" required/>
							<div class="invalid-feedback">Please enter a password.</div>
						</div>
						<div>
							<label for="newUser_confirmPassword" class="form-label">Confirm Password</label>
							<input type="password" class="form-control" id="newUser_confirmPassword" name="confirmPassword" required/>
							<div class="invalid-feedback">Please confirm your password.</div>
						</div>
						<div class="my-3 text-danger" id="userFormError"></div>
						<button type="submit" class="btn btn-outline-success">Submit</button>
					</form>
				</div>
			</div>
		</div>
	</div>
	</sec:authorize>
	<script src="<c:url value="/resources/js/bootstrap-5.2.0.bundle.min.js"/>"></script>
	<script src="<c:url value="/resources/js/jquery-3.6.0.min.js"/>"></script>
	<script src="<c:url value="/resources/js/jquery-ui-1.13.2.min.js"/>"></script>
	<script src="<c:url value="/resources/js/viewer.js"/>"></script>
</body>
</html>