<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Shopping Cart" />
</jsp:include>

<c:choose>
    <c:when test="${empty sessionScope.cart or sessionScope.cart.itemCount == 0}">
        <div class="text-center py-5">
            <div class="mb-4">
                <i class="fas fa-shopping-cart fa-5x text-muted"></i>
            </div>
            <h3>Your cart is empty</h3>
            <p class="lead">Browse our menu to add some delicious pizzas!</p>
            <a href="${pageContext.request.contextPath}/menu" class="btn btn-primary">Go to Menu</a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="table-responsive">
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>Item</th>
                        <th>Price</th>
                        <th>Quantity</th>
                        <th>Subtotal</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${sessionScope.cart.items}" var="item" varStatus="status">
                        <tr>
                            <td>
                                <strong>${item.pizza.name}</strong>
                                <c:if test="${not empty item.toppings}">
                                    <small class="d-block text-muted">
                                        Extra toppings: 
                                        <c:forEach items="${item.toppings}" var="topping" varStatus="toppingStatus">
                                            ${topping.name}${!toppingStatus.last ? ', ' : ''}
                                        </c:forEach>
                                    </small>
                                </c:if>
                            </td>
                            <td>
                                $<fmt:formatNumber value="${item.itemPrice}" type="currency" currencySymbol="" />
                                <c:if test="${not empty item.toppings}">
                                    <small class="d-block text-muted">
                                        Base: $<fmt:formatNumber value="${item.pizza.price}" type="currency" currencySymbol="" /><br>
                                        Toppings: $<fmt:formatNumber value="${item.toppingsCost}" type="currency" currencySymbol="" />
                                    </small>
                                </c:if>
                            </td>
                            <td>
                                <form action="${pageContext.request.contextPath}/cart/update" method="post" class="form-inline">
                                    <input type="hidden" name="action" value="update">
                                    <input type="hidden" name="itemIndex" value="${status.index}">
                                    <input type="number" class="form-control form-control-sm" style="width: 60px;" 
                                           name="quantity" value="${item.quantity}" min="1" max="10">
                                    <button type="submit" class="btn btn-sm btn-outline-secondary ml-1">
                                        <i class="fas fa-sync-alt"></i>
                                    </button>
                                </form>
                            </td>
                            <td>$<fmt:formatNumber value="${item.subtotal}" type="currency" currencySymbol="" /></td>
                            <td>
                                <form action="${pageContext.request.contextPath}/cart/update" method="post">
                                    <input type="hidden" name="action" value="remove">
                                    <input type="hidden" name="itemIndex" value="${status.index}">
                                    <button type="submit" class="btn btn-sm btn-danger">
                                        <i class="fas fa-trash"></i> Remove
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
                <tfoot>
                    <tr>
                        <th colspan="3" class="text-right">Total:</th>
                        <th>$<fmt:formatNumber value="${sessionScope.cart.total}" type="currency" currencySymbol="" /></th>
                        <th></th>
                    </tr>
                </tfoot>
            </table>
        </div>
        
        <div class="d-flex justify-content-between mt-4">
            <form action="${pageContext.request.contextPath}/cart/update" method="post">
                <input type="hidden" name="action" value="clear">
                <button type="submit" class="btn btn-outline-danger">
                    <i class="fas fa-trash"></i> Clear Cart
                </button>
            </form>
            
            <div>
                <a href="${pageContext.request.contextPath}/menu" class="btn btn-outline-secondary mr-2">
                    <i class="fas fa-pizza-slice"></i> Continue Shopping
                </a>
                <a href="${pageContext.request.contextPath}/checkout" class="btn btn-success">
                    <i class="fas fa-shopping-bag"></i> Checkout
                </a>
            </div>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="../common/footer.jsp" />