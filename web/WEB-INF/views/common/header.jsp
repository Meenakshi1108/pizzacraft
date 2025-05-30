<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${param.title} - PizzaCraft</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/">
                <i class="fas fa-pizza-slice"></i> PizzaCraft
            </a>
            <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav mr-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/">Home</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/menu">Menu</a>
                    </li>
                </ul>
                <ul class="navbar-nav">
                    <c:choose>
                        <c:when test="${not empty sessionScope.user}">
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/cart">
                                    <i class="fas fa-shopping-cart"></i> Cart
                                    <c:if test="${not empty sessionScope.cart and sessionScope.cart.totalQuantity > 0}">
                                        <span class="badge badge-pill badge-primary">${sessionScope.cart.totalQuantity}</span>
                                    </c:if>
                                </a>
                            </li>
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown">
                                    <i class="fas fa-user"></i> ${sessionScope.user.username}
                                </a>
                                <div class="dropdown-menu dropdown-menu-right">
                                    <c:if test="${sessionScope.user.role eq 'ADMIN'}">
                                        <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/dashboard">
                                            <i class="fas fa-tachometer-alt"></i> Admin Dashboard
                                        </a>
                                        <div class="dropdown-divider"></div>
                                    </c:if>
                                    <a class="dropdown-item" href="${pageContext.request.contextPath}/orders">
                                        <i class="fas fa-list"></i> My Orders
                                    </a>
                                    <a class="dropdown-item" href="${pageContext.request.contextPath}/profile">
                                        <i class="fas fa-user-edit"></i> Profile
                                    </a>
                                    <div class="dropdown-divider"></div>
                                    <a class="dropdown-item" href="${pageContext.request.contextPath}/logout">
                                        <i class="fas fa-sign-out-alt"></i> Logout
                                    </a>
                                </div>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/login">Login</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/register">Register</a>
                            </li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>
        </div>
    </nav>
    
    <c:if test="${not empty sessionScope.flashMessage}">
        <div class="container mt-3">
            <div class="alert alert-${sessionScope.flashType} alert-dismissible fade show">
                ${sessionScope.flashMessage}
                <button type="button" class="close" data-dismiss="alert">&times;</button>
            </div>
        </div>
        <c:remove var="flashMessage" scope="session" />
        <c:remove var="flashType" scope="session" />
    </c:if>
    
    <div class="container my-4">
        <h1 class="mb-4">${param.title}</h1>