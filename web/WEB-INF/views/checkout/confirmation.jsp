<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Order Confirmation" />
</jsp:include>

<c:if test="${not empty error}">
    <div class="alert alert-danger">${error}</div>
</c:if>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card border-success mb-4">
                <div class="card-header bg-success text-white">
                    <h4 class="mb-0">
                        <i class="fas fa-check-circle me-2"></i> Order Confirmed!
                    </h4>
                </div>
                <div class="card-body">
                    <div class="text-center mb-4">
                        <h2>Thank you for your order!</h2>
                        <p class="lead">Your order #${order.id} has been placed successfully.</p>
                        <p>We're preparing your delicious pizza right now. You can track the status of your delivery below.</p>
                    </div>
                    
                    <div class="alert alert-info">
                        <h5>Estimated Delivery Time:</h5>
                        <p class="mb-0">30-45 minutes from now</p>
                    </div>
                    
                    <h5 class="mt-4">Delivery Details:</h5>
                    <div class="card mb-3">
                        <div class="card-body">
                            <div class="row">
                                <div class="col-md-6">
                                    <p class="mb-1"><strong>Delivery Address:</strong></p>
                                    <p class="mb-3">${order.deliveryAddress}</p>
                                </div>
                                <div class="col-md-6">
                                    <p class="mb-1"><strong>Contact Number:</strong></p>
                                    <p class="mb-0">${order.contactNumber}</p>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <h5>Order Summary:</h5>
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Item</th>
                                    <th>Quantity</th>
                                    <th class="text-end">Price</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${order.orderItems}" var="item">
                                    <tr>
                                        <td>
                                            ${item.pizza.name}
                                            <c:if test="${not empty item.toppings && item.toppings.size() > 0}">
                                                <small class="d-block text-muted">
                                                    + ${item.toppings.size()} toppings
                                                </small>
                                            </c:if>
                                        </td>
                                        <td>${item.quantity}</td>
                                        <td class="text-end">₹<fmt:formatNumber value="${item.price}" type="number" minFractionDigits="2" maxFractionDigits="2" /></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                            <tfoot>
                                <tr>
                                    <th colspan="2">Total Amount:</th>
                                    <th class="text-end">₹<fmt:formatNumber value="${order.totalAmount}" type="number" minFractionDigits="2" maxFractionDigits="2" /></th>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
                    
                    <div class="d-grid gap-2 mt-4">
                        <a href="${pageContext.request.contextPath}/view-order/${order.id}" class="btn btn-primary">
                            <i class="fas fa-pizza-slice me-2"></i> Track Order
                        </a>
                        <a href="${pageContext.request.contextPath}/menu" class="btn btn-outline-secondary">
                            <i class="fas fa-utensils me-2"></i> Order More
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />