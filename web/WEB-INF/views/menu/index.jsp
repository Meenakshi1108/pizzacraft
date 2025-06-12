<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- 
  Pizza Menu View - Displays all pizzas or filtered pizzas by category/type
  Supports filtering by vegetarian and by pizza category
--%>
<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="${not empty selectedCategory ? selectedCategory.name : not empty filterTitle ? filterTitle : 'Menu'}" />
</jsp:include>

<div class="container py-4">
    <div class="row">
        <div class="col-lg-3 col-md-4">
            <div class="card mb-4 shadow-sm">
                <div class="card-header bg-dark text-white">
                    <i class="fas fa-list me-2"></i> Categories
                </div>
                <div class="list-group list-group-flush">
                    <a href="${pageContext.request.contextPath}/menu" 
                       class="list-group-item list-group-item-action ${empty selectedCategory && empty filterTitle ? 'active' : ''}">
                        <i class="fas fa-pizza-slice me-2"></i> All Pizzas
                    </a>
                    <a href="${pageContext.request.contextPath}/menu?filter=vegetarian" 
                       class="list-group-item list-group-item-action ${filterTitle eq 'Vegetarian Pizzas' ? 'active' : ''}">
                        <i class="fas fa-leaf me-2"></i> Vegetarian Pizzas
                    </a>
                    <c:forEach items="${categories}" var="category">
                        <a href="${pageContext.request.contextPath}/menu?category=${category.id}" 
                           class="list-group-item list-group-item-action ${selectedCategory.id eq category.id ? 'active' : ''}">
                            <i class="fas fa-utensils me-2"></i> ${category.name}
                        </a>
                    </c:forEach>
                </div>
            </div>
        </div>
        
        <div class="col-lg-9 col-md-8">
            <c:choose>
                <c:when test="${empty pizzas}">
                    <div class="alert alert-info">
                        <i class="fas fa-info-circle me-2"></i>
                        No pizzas found in this category.
                    </div>
                </c:when>                <c:otherwise>
                    <div class="row row-cols-1 row-cols-sm-2 row-cols-md-2 row-cols-lg-3 g-4">
                        <c:forEach items="${pizzas}" var="pizza">
                            <div class="col">
                                <div class="card h-100 pizza-card">
                                    <div class="pizza-image-container">                                        <c:choose>
                                            <c:when test="${not empty pizza.imageUrl}">
                                                <img src="${pizza.imageUrl}" class="card-img-top" alt="${pizza.name}" 
                                                     loading="lazy" fetchpriority="high">
                                            </c:when>
                                            <c:otherwise>
                                                <img src="${pageContext.request.contextPath}/images/pizza-placeholder.jpg" 
                                                     class="card-img-top" alt="${pizza.name}" loading="lazy">
                                            </c:otherwise>
                                        </c:choose>
                                        <div class="pizza-badge ${pizza.vegetarian ? 'veg' : 'non-veg'}">
                                            <span class="badge rounded-pill bg-${pizza.vegetarian ? 'success' : 'danger'} fs-7">
                                                ${pizza.vegetarian ? 'Veg' : 'Non-Veg'}
                                            </span>
                                        </div>
                                    </div>
                                    
                                    <div class="card-body">
                                        <h5 class="card-title">${pizza.name}</h5>
                                        <p class="card-text description-truncate mb-3">${pizza.description}</p>
                                        <div class="d-flex justify-content-between align-items-center">
                                            <span class="badge bg-light text-dark">${pizza.category.name}</span>
                                            <p class="price mb-0">
                                                ₹<fmt:formatNumber value="${pizza.price}" type="number" minFractionDigits="2" maxFractionDigits="2" />
                                            </p>
                                        </div>
                                    </div>
                                    <div class="card-footer bg-white border-top-0 pt-0">
                                        <c:choose>
                                            <c:when test="${pizza.available}">
                                                <a href="${pageContext.request.contextPath}/pizza/${pizza.id}" 
                                                   class="btn btn-primary btn-sm w-100">
                                                   <i class="fas fa-info-circle me-1"></i> View Details
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <button class="btn btn-secondary btn-sm w-100" disabled>
                                                    <i class="fas fa-ban me-1"></i> Currently Unavailable
                                                </button>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />