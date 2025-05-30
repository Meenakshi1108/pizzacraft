<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="${pizza.name}" />
</jsp:include>

<div class="row">
    <div class="col-md-5">
        <c:choose>
            <c:when test="${not empty pizza.imageUrl}">
                <img src="${pizza.imageUrl}" class="img-fluid rounded" alt="${pizza.name}">
            </c:when>
            <c:otherwise>
                <img src="${pageContext.request.contextPath}/images/pizza-placeholder.jpg" 
                     class="img-fluid rounded" alt="${pizza.name}">
            </c:otherwise>
        </c:choose>
    </div>
    
    <div class="col-md-7">
        <h2>${pizza.name}</h2>
        <p class="lead">${pizza.description}</p>
        
        <div class="mb-3">
            <span class="badge ${pizza.vegetarian ? 'badge-success' : 'badge-danger'} mr-2">
                ${pizza.vegetarian ? 'Vegetarian' : 'Non-Vegetarian'}
            </span>
            <span class="badge badge-info">${pizza.category.name}</span>
        </div>
        
        <h3 class="mb-4">₹<fmt:formatNumber value="${pizza.price}" type="number" minFractionDigits="2" maxFractionDigits="2" /></h3>
        
        <c:if test="${pizza.available}">
            <form action="${pageContext.request.contextPath}/cart" method="post">
                <input type="hidden" name="pizzaId" value="${pizza.id}">
                
                <div class="form-group">
                    <label for="quantity">Quantity:</label>
                    <input type="number" class="form-control" id="quantity" name="quantity" 
                           value="1" min="1" max="10" required style="width: 100px;">
                </div>
                
                <c:if test="${not empty toppings}">
                    <div class="form-group">
                        <label>Add Extra Toppings:</label>
                        <div class="row">
                            <c:forEach items="${toppings}" var="topping">
                                <div class="col-md-6 mb-2">
                                    <div class="custom-control custom-checkbox">
                                        <input type="checkbox" class="custom-control-input" 
                                               id="topping${topping.id}" name="toppingIds" value="${topping.id}">
                                        <label class="custom-control-label" for="topping${topping.id}">
                                            ${topping.name} (+₹<fmt:formatNumber value="${topping.price}" type="number" minFractionDigits="2" maxFractionDigits="2" />)
                                        </label>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </c:if>
                
                <button type="submit" class="btn btn-primary btn-lg">
                    <i class="fas fa-cart-plus"></i> Add to Cart
                </button>
            </form>
        </c:if>
        
        <c:if test="${!pizza.available}">
            <div class="alert alert-secondary">
                This pizza is currently unavailable.
            </div>
        </c:if>
        
        <div class="mt-4">
            <a href="${pageContext.request.contextPath}/menu" class="btn btn-outline-secondary">
                <i class="fas fa-arrow-left"></i> Back to Menu
            </a>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />