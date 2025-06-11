<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Shopping Cart" />
</jsp:include>
<!-- Add custom CSS for cart fixes -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/cart-fixes.css">

<div class="container py-4 mb-5">
    <h2 class="mb-4">
        <i class="fas fa-shopping-cart me-2"></i> Your Cart
    </h2>    <c:choose>
        <c:when test="${empty cart or cart.itemCount == 0}">
            <div class="text-center py-5">
                <div class="mb-4">
                    <i class="fas fa-shopping-cart fa-5x text-muted"></i>
                </div>
                <h3>Your cart is empty</h3>
                <p class="lead">Looks like you haven't added any items to your cart yet.</p>
                <a href="${pageContext.request.contextPath}/menu" class="btn btn-primary btn-lg mt-3">
                    <i class="fas fa-pizza-slice me-2"></i> Browse Menu
                </a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="card shadow-sm mb-4">
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table align-middle">
                            <thead>
                                <tr>
                                    <th style="width: 80px;"></th>
                                    <th>Item</th>
                                    <th>Price</th>
                                    <th style="width: 160px;">Quantity</th>
                                    <th class="text-end">Subtotal</th>
                                    <th style="width: 50px;"></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${not empty cart ? cart.items : []}" var="item" varStatus="status">
                                    <!-- Only try to access item properties if item is not null -->
                                    <c:if test="${not empty item}">
                                        <tr>                        <td>
                            <c:choose>
                                <c:when test="${not empty item.pizza.imageUrl}">
                                    <img src="${item.pizza.imageUrl}" alt="${item.pizza.name}" class="cart-item-image rounded shadow-sm">
                                </c:when>
                                <c:otherwise>
                                    <img src="${pageContext.request.contextPath}/images/pizza-placeholder.jpg"
                                         alt="${item.pizza.name}" class="cart-item-image rounded shadow-sm">
                                            </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <h5 class="mb-1">${item.pizza.name}</h5>
                                            <c:if test="${not empty item.toppings and item.toppings.size() > 0}">
                                                <small class="text-muted">
                                                    Extra toppings: 
                                                    <c:forEach items="${item.toppings}" var="topping" varStatus="toppingStatus">
                                                        ${topping.name}<c:if test="${!toppingStatus.last}">, </c:if>
                                                    </c:forEach>
                                                </small>
                                            </c:if>
                                        </td>
                                        <td>
                                            ₹<fmt:formatNumber value="${item.unitPrice}" type="number" 
                                                minFractionDigits="2" maxFractionDigits="2" />
                                        </td>
                                        <td>
                                            <form action="${pageContext.request.contextPath}/cart" method="post" class="quantity-form">
                                                <input type="hidden" name="action" value="update">
                                                <input type="hidden" name="itemIndex" value="${status.index}">
                                                <div class="input-group">
                                                    <button type="button" class="btn btn-outline-secondary btn-sm quantity-decrement" 
                                                            aria-label="Decrease quantity">
                                                        <i class="fas fa-minus"></i>
                                                    </button>
                                                    <input type="number" class="form-control form-control-sm text-center quantity-input" 
                                                           name="quantity" min="1" max="10" value="${item.quantity}"
                                                           data-unit-price="${item.unitPrice}">                                                    <button type="button" class="btn btn-outline-secondary btn-sm quantity-increment" 
                                                            aria-label="Increase quantity" title="Increase quantity">
                                                        <i class="fas fa-plus"></i>
                                                    </button>
                                                    <!-- Refresh button removed as requested -->
                                                </div>
                                            </form>
                                        </td>
                                        <td class="text-end subtotal">
                                            <c:if test="${not empty item and not empty item.subtotal}">₹${item.subtotal}</c:if>
                                            <c:if test="${empty item or empty item.subtotal}">₹0.00</c:if>
                                        </td>
                                        <td>
                                            <form action="${pageContext.request.contextPath}/cart" method="post">
                                                <input type="hidden" name="action" value="remove">
                                                <input type="hidden" name="itemIndex" value="${status.index}">
                                                <button type="submit" class="btn btn-sm btn-outline-danger" title="Remove item">
                                                    <i class="fas fa-trash-alt"></i>
                                                </button>
                                            </form>
                                        </td>
                                    </tr>
                                    </c:if>
                                </c:forEach>
                            </tbody>
                            <tfoot class="table-group-divider">
                                <tr class="fw-bold">
                                    <td colspan="4" class="text-end">Total:</td>
                                    <td class="text-end total">
                                        ₹<fmt:formatNumber value="${sessionScope.cart.total}" type="number" 
                                            minFractionDigits="2" maxFractionDigits="2" />
                                    </td>
                                    <td></td>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
                </div>
            </div>
            
            <div class="d-flex justify-content-between">
                <a href="${pageContext.request.contextPath}/menu" class="btn btn-outline-secondary">
                    <i class="fas fa-arrow-left me-2"></i> Continue Shopping
                </a>
                <div>
                    <form action="${pageContext.request.contextPath}/cart" method="post" class="d-inline">
                        <input type="hidden" name="action" value="clear">
                        <button type="submit" class="btn btn-outline-danger me-2">
                            <i class="fas fa-trash me-2"></i> Clear Cart
                        </button>
                    </form>
                    <a href="${pageContext.request.contextPath}/checkout" class="btn btn-primary">
                        <i class="fas fa-shopping-bag me-2"></i> Proceed to Checkout
                    </a>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- Add extra space before footer -->
<div class="mb-5"></div>

<jsp:include page="../common/footer.jsp" />

<!-- Cart functionality script -->
<script src="${pageContext.request.contextPath}/js/cart.js"></script>
