<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <meta http-equiv="X-UA-Compatible" content="IE=edge" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Web DBViewer | Access Denied</title>
        <link rel="stylesheet" href="<c:url value="/resources/css/bootstrap-5.2.0.min.css"/>" />
    </head>
    <body>
        <div class="d-flex align-items-center justify-content-center vh-100">
            <div class="text-center">
                <h1 class="display-1 fw-bold text-danger">Access Denied</h1>
                <p class="lead">
                   You do not have permission to view the requested page or resource.
                </p>
                <a href="<c:url value="/"/>" class="btn btn-primary">Go Home</a>
            </div>
        </div>
    </body>
</html>