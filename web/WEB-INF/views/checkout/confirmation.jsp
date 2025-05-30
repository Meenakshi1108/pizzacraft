<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Checkout" />
</jsp:include>

<c:if test="${not empty error}">
    <div class="alert alert-danger">${error}</div>
</c:if>

<div class="row">
    <div class="col-md-8">
        <div class="card mb-4">
            <div class="card-header">Delivery Information</div>
            <div class="card-body">
                <form method="post" action="${pageContext.request.contextPath}/checkout">
                    <div class="form-group">
                        <label for="deliveryAddress">Delivery Address</label>
                        <textarea class="form-control" id="deliveryAddress" name="deliveryAddress" 
                                  rows="3" required>${deliveryAddress}</textarea>
                    </div>
                    
                    <div class="form-group">
                        <label for="contactNumber">Contact Number</label>
                        <input type="tel" class="form-control" id="contactNumber" name="contactNumber" 
                               value="${contactNumber || user.phone}" required>
                    </div>
                    
                    <button type="submit" class="btn btn-primary btn-lg">Place Order</button>
                </form>
            </div>
        </div>
    </div>
    
    <div class="col-md-4">
        <div class="card">
            <div class="card-header">Order Summary</div>
            <div class="card-body">
                <h5>Items (${sessionScope.cart.totalQuantity})</h5>
                <ul class="list-group list-group-flush mb-3">
                    <c:forEach items="${sessionScope.cart.items}" var="item">
                        <li class="list-group-item d-flex justify-content-between align-items-center">
                            <div>
                                ${item.quantity} x ${item.pizza.name}
                                <c:if test="${not empty item.toppings}">
                                    <small class="d-block text-muted">
                                        + ${item.toppings.size()} toppings
                                    </small>
                                </c:if>
                            </div>
                            <span>$<fmt:formatNumber value="${item.subtotal}" type="currency" currencySymbol="" /></span>
                        </li>
                    </c:forEach>
                </ul>
                
                <div class="d-flex justify-content-between font-weight-bold">
                    <span>Total Amount:</span>
                    <span>$<fmt:formatNumber value="${sessionScope.cart.total}" type="currency" currencySymbol="" /></span>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />