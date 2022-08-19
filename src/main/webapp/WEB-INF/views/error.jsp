<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <meta http-equiv="X-UA-Compatible" content="IE=edge" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Web DBViewer | Error</title>
        <link rel="stylesheet" href="<c:url value="/resources/css/bootstrap-5.2.0.min.css"/>" />
        <style>html, body {height: 100%;}</style>
    </head>
    <body>
        <div class="d-flex flex-column ${showRawError ? '' : 'justify-content-center align-items-center h-100'}">
            <div class="text-center">
            	<h1 class="display-1 fw-bold text-danger">An error occurred!</h1>
                <p class="lead">
                    Please check your request and try again.
                 </p>
                <a href="<c:url value="/"/>" class="btn btn-primary">Go Home</a>
            </div>
            <c:if test="${showRawError}">
	            <div class="mt-2 mx-2">
	            	<h2>Details</h2>
		            <table class="table table-bordered">
		            	<tr><td>Date</td><td>${timestamp}</td></tr>
		            	<tr><td>Path</td><td>${path}</td></tr>
		            	<tr><td>Error</td><td>${error}</td></tr>
		            	<tr><td>Status</td><td>${status}</td></tr>
		            	<tr><td>Message</td><td>${message}</td></tr>
		            	<tr><td>Exception</td><td>${exception}</td></tr>
		            	<tr><td>Trace</td><td><pre><c:out value="${trace}"/></pre></td></tr>
		            </table>
	            </div>
            </c:if>
        </div>
    </body>
</html>