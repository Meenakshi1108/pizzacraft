<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%-- 
  Pizza Detail View - Shows detailed information about a specific pizza
  Allows users to customize and add pizza to cart
--%>
<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="${pizza.name}" />
</jsp:include>

<div class="container py-4">
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/">Home</a></li>
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/menu">Menu</a></li>
            <li class="breadcrumb-item active" aria-current="page">${pizza.name}</li>
        </ol>
    </nav>
    
    <div class="card shadow">
        <div class="card-body p-4">
            <div class="row g-4">                <div class="col-md-5">
                    <div class="pizza-detail-image-container position-relative">
                        <c:choose>
                            <c:when test="${not empty pizza.imageUrl}">
                                <!-- Fix the image path by prepending the context path -->
                                <img src="${pageContext.request.contextPath}/${pizza.imageUrl}" 
                                     class="img-fluid rounded pizza-detail-img" 
                                     alt="${pizza.name}" 
                                     loading="eager" 
                                     onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/images/pizza-placeholder.jpg'; document.getElementById('imgError').style.display='block';">
                            </c:when>
                            <c:otherwise>
                                <img src="${pageContext.request.contextPath}/images/pizza-placeholder.jpg" 
                                     class="img-fluid rounded pizza-detail-img" alt="${pizza.name}" loading="eager">
                            </c:otherwise>
                        </c:choose>
                        <span class="badge bg-${pizza.vegetarian ? 'success' : 'danger'} position-absolute top-0 end-0 m-3 fs-6">
                            ${pizza.vegetarian ? 'Vegetarian' : 'Non-Vegetarian'}
                        </span>
                        <div id="imgError" class="image-error-message" style="display: none;">
                            Image could not be loaded. Displaying placeholder.
                        </div>
                    </div>
                </div>
                
                <div class="col-md-7">
                    <h2 class="mb-3 fw-bold">${pizza.name}</h2>
                    
                    <p class="lead mb-4">${pizza.description}</p>
                      <div class="mb-3">
                        <span class="badge bg-light text-dark me-2 p-2">
                            <i class="fas fa-tag me-1"></i> ${pizza.category.name}
                        </span>
                        <!-- Removed spiceLevel section as it's not in the Pizza model -->
                    </div>
                    
                    <h3 class="price mb-4">₹<fmt:formatNumber value="${pizza.price}" type="number" minFractionDigits="2" maxFractionDigits="2" /></h3>
                    
                    <c:if test="${pizza.available}">
                        <form action="${pageContext.request.contextPath}/cart" method="post" class="mb-4">
                            <input type="hidden" name="action" value="add">
                            <input type="hidden" name="pizzaId" value="${pizza.id}">
                            
                            <c:if test="${not empty toppings}">
                                <div class="card shadow-sm mb-3">
                                    <div class="card-header bg-light">
                                        <h5 class="mb-0"><i class="fas fa-plus-circle me-2"></i> Add Extra Toppings</h5>
                                    </div>
                                    <div class="card-body">
                                        <div class="row">
                                            <c:forEach items="${toppings}" var="topping">
                                                <div class="col-6 mb-2">
                                                    <div class="form-check">
                                                        <input class="form-check-input" type="checkbox" name="toppings" 
                                                               value="${topping.id}" id="topping${topping.id}">
                                                        <label class="form-check-label" for="topping${topping.id}">
                                                            ${topping.name} (+₹${topping.price})
                                                        </label>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </div>
                            </c:if>
                            
                            <div class="row g-3 align-items-center mb-4">
                                <div class="col-auto">
                                    <label for="quantity" class="col-form-label fw-bold">Quantity:</label>
                                </div>
                                <div class="col-auto">
                                    <div class="input-group quantity-selector">
                                        <button type="button" class="btn btn-outline-secondary quantity-decrement" 
                                                aria-label="Decrease quantity">
                                            <i class="fas fa-minus"></i>
                                        </button>
                                        <input type="number" class="form-control text-center" id="quantity" name="quantity" 
                                               min="1" max="10" value="1" style="width: 60px;">
                                        <button type="button" class="btn btn-outline-secondary quantity-increment" 
                                                aria-label="Increase quantity">
                                            <i class="fas fa-plus"></i>
                                        </button>
                                    </div>
                                </div>
                            </div>
                            
                            <c:choose>
                                <c:when test="${sessionScope.user != null}">
                                    <button type="submit" class="btn btn-primary btn-lg">
                                        <i class="fas fa-cart-plus me-2"></i> Add to Cart
                                    </button>
                                </c:when>
                                <c:otherwise>
                                    <button type="button" class="btn btn-primary btn-lg" data-bs-toggle="modal" data-bs-target="#loginModal">
                                        <i class="fas fa-user me-2"></i> Login to Add to Cart
                                    </button>
                                </c:otherwise>
                            </c:choose>
                        </form>
                    </c:if>
                    
                    <c:if test="${!pizza.available}">
                        <div class="alert alert-warning">
                            <i class="fas fa-exclamation-triangle me-2"></i>
                            This item is currently unavailable.
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Login Modal -->
<c:if test="${sessionScope.user == null}">
    <div class="modal fade" id="loginModal" tabindex="-1" aria-labelledby="loginModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="loginModalLabel">Login Required</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <p>Please log in to add items to your cart.</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">Go to Login</a>
                </div>
            </div>
        </div>
    </div>
</c:if>

<jsp:include page="../common/footer.jsp" />