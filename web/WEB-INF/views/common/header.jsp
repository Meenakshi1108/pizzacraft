<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- 
  Common Header - Included in all pages
  Contains navigation, user dropdown menu, and CSS/JS includes
--%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${param.title} - PizzaCraft</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;600;700&display=swap" rel="stylesheet"><!-- Add these lines for faster resource loading -->
    <link rel="dns-prefetch" href="//cdn.jsdelivr.net">
    <link rel="preconnect" href="https://cdn.jsdelivr.net" crossorigin>    <!-- Custom CSS -->
    <!-- Base styles first -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <!-- Then specialized styles -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hero.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pizza-detail.css">    <!-- Then overrides/fixes -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive-fixes.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/extra-fixes.css">    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/performance-optimizations.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cart-fixes.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/layout-fixes.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hero-fixes.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/order-details-fixes.css">
</head>
<body>
    <div class="content-wrapper">
    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top">
        <div class="container">            <a class="navbar-brand" href="${pageContext.request.contextPath}/">
                <img src="${pageContext.request.contextPath}/images/logo.png" alt="PizzaCraft" height="50">
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/">Home</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/menu">Menu</a>
                    </li>
                </ul>
                
                <ul class="navbar-nav">
                    <!-- Cart link with badge showing item count -->
                    <li class="nav-item">
                        <a class="nav-link position-relative" href="${pageContext.request.contextPath}/cart">
                            <i class="fas fa-shopping-cart"></i> Cart
                            <c:if test="${not empty sessionScope.cart and sessionScope.cart.itemCount > 0}">
                                <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger cart-badge">
                                    ${sessionScope.cart.itemCount}
                                    <span class="visually-hidden">items in cart</span>
                                </span>
                            </c:if>
                        </a>
                    </li>
                    
                    <c:choose>
                        <c:when test="${empty sessionScope.user}">
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/login">Login</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/register">Register</a>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" 
                                   data-bs-toggle="dropdown" aria-expanded="false">
                                    <i class="fas fa-user-circle"></i> ${sessionScope.user.username}
                                </a>
                                <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                                    <c:if test="${sessionScope.user.role == 'ADMIN'}">
                                        <li>
                                            <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/dashboard">
                                                <i class="fas fa-tachometer-alt me-2"></i> Dashboard
                                            </a>
                                        </li>
                                        <li><hr class="dropdown-divider"></li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.role == 'DELIVERY'}">
                                        <li>
                                            <a class="dropdown-item" href="${pageContext.request.contextPath}/delivery/dashboard">
                                                <i class="fas fa-motorcycle me-2"></i> My Deliveries
                                            </a>
                                        </li>
                                        <li><hr class="dropdown-divider"></li>
                                    </c:if>
                                    <li>
                                        <a class="dropdown-item" href="${pageContext.request.contextPath}/profile">
                                            <i class="fas fa-user-cog me-2"></i> My Profile
                                        </a>
                                    </li>
                                    <li>
                                        <a class="dropdown-item" href="${pageContext.request.contextPath}/orders">
                                            <i class="fas fa-clipboard-list me-2"></i> My Orders
                                        </a>
                                    </li>
                                    <li><hr class="dropdown-divider"></li>
                                    <li>
                                        <a class="dropdown-item" href="${pageContext.request.contextPath}/logout">
                                            <i class="fas fa-sign-out-alt me-2"></i> Logout
                                        </a>
                                    </li>
                                </ul>
                            </li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Flash Messages -->
    <c:if test="${not empty sessionScope.success}">
        <div class="alert alert-success alert-dismissible fade show m-0" role="alert">
            <div class="container">
                <i class="fas fa-check-circle me-2"></i> ${sessionScope.success}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </div>
        <c:remove var="success" scope="session" />
    </c:if>
    
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger alert-dismissible fade show m-0" role="alert">
            <div class="container">
                <i class="fas fa-exclamation-circle me-2"></i> ${sessionScope.error}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </div>
        <c:remove var="error" scope="session" />
    </c:if>
    
    <!-- Add custom CSS for the badge styling -->
    <style>
    .cart-badge {
        font-size: 0.6rem;
        padding: 0.25rem 0.4rem;
        margin-left: -0.5rem;
        transform: translateY(-50%);
    }
    
    /* Animation for when item added to cart */
    @keyframes pulse {
        0% { transform: scale(1); }
        50% { transform: scale(1.2); }
        100% { transform: scale(1); }
    }
    
    .cart-badge-animate {
        animation: pulse 0.5s ease-in-out;
    }
    </style>

    <!-- Main Content -->