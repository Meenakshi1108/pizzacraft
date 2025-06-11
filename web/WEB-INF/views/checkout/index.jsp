<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Checkout" />
</jsp:include>

<div class="container py-4">
    <h2 class="mb-4">
        <i class="fas fa-shopping-bag me-2"></i> Checkout
    </h2>
    
    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-circle me-2"></i> ${error}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    
    <div class="alert alert-info" role="alert">
        <i class="fas fa-info-circle me-2"></i> We only support Cash on Delivery at this time.
    </div>

    <div class="row">
        <div class="col-lg-8">
            <div class="card mb-4 shadow-sm">
                <div class="card-header bg-white">
                    <h5 class="mb-0">
                        <i class="fas fa-map-marker-alt me-2"></i> Delivery Information
                    </h5>
                </div>
                <div class="card-body">
                    <form method="post" action="${pageContext.request.contextPath}/checkout">
                        <div class="mb-3">
                            <label for="deliveryAddress" class="form-label">Delivery Address *</label>
                            <textarea class="form-control" id="deliveryAddress" name="deliveryAddress" 
                                      rows="3" required>${deliveryAddress}</textarea>
                        </div>
                        
                        <div class="mb-4">
                            <label for="contactNumber" class="form-label">Contact Number *</label>
                            <input type="tel" class="form-control" id="contactNumber" name="contactNumber" 
                                   value="${contactNumber}" required
                                   placeholder="Enter your phone number for delivery updates">
                            <div class="form-text">We'll contact you at this number if there are any issues with delivery.</div>
                        </div>
                        
                        <button type="submit" class="btn btn-primary btn-lg">
                            <i class="fas fa-check-circle me-2"></i> Place Order
                        </button>
                    </form>
                </div>
            </div>
        </div>
        
        <div class="col-lg-4">
            <div class="card shadow-sm">
                <div class="card-header bg-white">
                    <h5 class="mb-0">
                        <i class="fas fa-receipt me-2"></i> Order Summary
                    </h5>
                </div>
                <div class="card-body">
                    <h6 class="mb-3">Items (${sessionScope.cart.itemCount})</h6>
                    <ul class="list-group list-group-flush mb-3">
                        <c:forEach items="${sessionScope.cart.items}" var="item">
                            <li class="list-group-item px-0 d-flex justify-content-between">
                                <div>
                                    <span class="fw-bold">${item.quantity}x</span> ${item.pizza.name}
                                    <c:if test="${not empty item.toppings and item.toppings.size() > 0}">
                                        <small class="d-block text-muted">
                                            + ${item.toppings.size()} toppings
                                        </small>
                                    </c:if>
                                </div>
                                <span class="text-end">
                                    ₹<fmt:formatNumber value="${item.subtotal}" type="number" 
                                             minFractionDigits="2" maxFractionDigits="2" />
                                </span>
                            </li>
                        </c:forEach>
                    </ul>
                    
                    <div class="d-flex justify-content-between fw-bold border-top pt-3 mt-3">
                        <span>Total Amount:</span>
                        <span>₹<fmt:formatNumber value="${sessionScope.cart.total}" type="number" 
                                   minFractionDigits="2" maxFractionDigits="2" /></span>
                    </div>
                    
                    <div class="alert alert-success mt-3 mb-0">
                        <i class="fas fa-money-bill-wave me-2"></i> Cash on Delivery
                    </div>
                </div>
            </div>
            
            <div class="mt-3">
                <a href="${pageContext.request.contextPath}/cart" class="btn btn-outline-secondary w-100">
                    <i class="fas fa-arrow-left me-2"></i> Back to Cart
                </a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />