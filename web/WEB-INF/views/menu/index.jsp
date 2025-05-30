<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="${not empty selectedCategory ? selectedCategory.name : not empty filterTitle ? filterTitle : 'Menu'}" />
</jsp:include>

<div class="row">
    <div class="col-md-3">
        <div class="card mb-4">
            <div class="card-header">Categories</div>
            <div class="list-group list-group-flush">
                <a href="${pageContext.request.contextPath}/menu" 
                   class="list-group-item list-group-item-action ${empty selectedCategory && empty filterTitle ? 'active' : ''}">
                    All Pizzas
                </a>
                <a href="${pageContext.request.contextPath}/menu?filter=vegetarian" 
                   class="list-group-item list-group-item-action ${filterTitle eq 'Vegetarian Pizzas' ? 'active' : ''}">
                    Vegetarian Pizzas
                </a>
                <c:forEach items="${categories}" var="category">
                    <a href="${pageContext.request.contextPath}/menu?category=${category.id}" 
                       class="list-group-item list-group-item-action ${selectedCategory.id eq category.id ? 'active' : ''}">
                        ${category.name}
                    </a>
                </c:forEach>
            </div>
        </div>
    </div>
    
    <div class="col-md-9">
        <c:choose>
            <c:when test="${empty pizzas}">
                <div class="alert alert-info">No pizzas found in this category.</div>
            </c:when>
            <c:otherwise>
                <div class="row">
                    <c:forEach items="${pizzas}" var="pizza">
                        <div class="col-md-4 mb-4">
                            <div class="card h-100">
                                <c:choose>
                                    <c:when test="${not empty pizza.imageUrl}">
                                        <img src="${pizza.imageUrl}" class="card-img-top" alt="${pizza.name}">
                                    </c:when>
                                    <c:otherwise>
                                            <img src="${pageContext.request.contextPath}/images/pizza-placeholder.jpg" 
                                             class="card-img-top" alt="${pizza.name}">
                                    </c:otherwise>
                                </c:choose>
                                
                                <div class="card-body">
                                    <h5 class="card-title">${pizza.name}</h5>
                                    <p class="card-text">${pizza.description}</p>
                                    <p class="card-text">
                                        <span class="badge ${pizza.vegetarian ? 'badge-success' : 'badge-danger'}">
                                            ${pizza.vegetarian ? 'Vegetarian' : 'Non-Vegetarian'}
                                        </span>
                                        <span class="badge badge-info">${pizza.category.name}</span>
                                    </p>
                                    <p class="card-text font-weight-bold">
                                        ₹<fmt:formatNumber value="${pizza.price}" type="number" minFractionDigits="2" maxFractionDigits="2" />
                                    </p>
                                </div>
                                <div class="card-footer">
                                    <c:choose>
                                        <c:when test="${pizza.available}">
                                            <a href="${pageContext.request.contextPath}/pizza/${pizza.id}" 
                                               class="btn btn-primary btn-block">Details</a>
                                        </c:when>
                                        <c:otherwise>
                                            <button class="btn btn-secondary btn-block" disabled>Currently Unavailable</button>
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

<jsp:include page="../common/footer.jsp" />