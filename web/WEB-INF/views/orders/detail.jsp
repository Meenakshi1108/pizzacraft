<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Order #${order.id}" />
</jsp:include>

<div class="card mb-4">
    <div class="card-header d-flex justify-content-between align-items-center">
        <div>
            Order Status: 
            <span class="badge 
                  ${order.orderStatus eq 'PLACED' ? 'badge-info' : 
                   order.orderStatus eq 'PREPARING' ? 'badge-primary' :
                   order.orderStatus eq 'READY' ? 'badge-warning' :
                   order.orderStatus eq 'OUT_FOR_DELIVERY' ? 'badge-dark' :
                   order.orderStatus eq 'DELIVERED' ? 'badge-success' :
                   'badge-danger'}">
                ${order.orderStatus}
            </span>
        </div>
        <div>
            <c:if test="${order.assignedToUserId != null}">
                <span class="badge badge-info">
                    <i class="fas fa-motorcycle"></i> Assigned to Delivery Person
                </span>
            </c:if>
        </div>
    </div>
    <div class="card-body">
        <div class="row">
            <div class="col-md-6">
                <h5>Delivery Information</h5>
                <p><strong>Address:</strong> ${order.deliveryAddress}</p>
                <p><strong>Contact Number:</strong> ${order.contactNumber}</p>
                <p><strong>Order Date:</strong> <fmt:formatDate value="${order.createdAt}" pattern="yyyy-MM-dd HH:mm" /></p>
                <c:if test="${order.assignedAt != null}">
                    <p><strong>Assigned at:</strong> <fmt:formatDate value="${order.assignedAt}" pattern="yyyy-MM-dd HH:mm" /></p>
                </c:if>
                <c:if test="${order.deliveredAt != null}">
                    <p><strong>Delivered at:</strong> <fmt:formatDate value="${order.deliveredAt}" pattern="yyyy-MM-dd HH:mm" /></p>
                </c:if>
            </div>
            <div class="col-md-6">
                <h5>Order Summary</h5>
                <p><strong>Total Items:</strong> ${order.orderItems.size()}</p>
                <p><strong>Total Amount:</strong> $<fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="" /></p>
                <p><strong>Payment Method:</strong> Cash on Delivery</p>
            </div>
        </div>
        
        <!-- Order Tracking -->
        <div class="my-4">
            <h5>Order Tracking</h5>
            <div class="row">
                <div class="col-md-12">
                    <ul class="list-unstyled">
                        <li class="media mb-3 tracking-step">
                            <div class="tracking-icon active mr-3">
                                <i class="fas fa-check"></i>
                            </div>
                            <div class="media-body">
                                <h6 class="mt-0 mb-1">Order Placed</h6>
                                <small class="text-muted">
                                    <fmt:formatDate value="${order.createdAt}" pattern="yyyy-MM-dd HH:mm" />
                                </small>
                            </div>
                        </li>
                        
                        <li class="media mb-3 tracking-step">
                            <div class="tracking-icon ${order.orderStatus eq 'PREPARING' || order.orderStatus eq 'READY' || 
                                                      order.orderStatus eq 'OUT_FOR_DELIVERY' || order.orderStatus eq 'DELIVERED' ? 'active' : ''} mr-3">
                                <i class="fas ${order.orderStatus eq 'PREPARING' || order.orderStatus eq 'READY' || 
                                               order.orderStatus eq 'OUT_FOR_DELIVERY' || order.orderStatus eq 'DELIVERED' ? 'fa-check' : 'fa-clock'}"></i>
                            </div>
                            <div class="media-body">
                                <h6 class="mt-0 mb-1">Preparing</h6>
                                <small class="text-muted">Your order is being prepared</small>
                            </div>
                        </li>
                        
                        <li class="media mb-3 tracking-step">
                            <div class="tracking-icon ${order.assignedToUserId != null ? 'active' : ''} mr-3">
                                <i class="fas ${order.assignedToUserId != null ? 'fa-check' : 'fa-motorcycle'}"></i>
                            </div>
                            <div class="media-body">
                                <h6 class="mt-0 mb-1">Ready for Delivery</h6>
                                <small class="text-muted">
                                    ${order.assignedToUserId != null ? 'Assigned to delivery person' : 'Waiting for delivery assignment'}
                                </small>
                            </div>
                        </li>
                        
                        <li class="media mb-3 tracking-step">
                            <div class="tracking-icon ${order.orderStatus eq 'OUT_FOR_DELIVERY' ? 'active' : ''} mr-3">
                                <i class="fas ${order.orderStatus eq 'OUT_FOR_DELIVERY' ? 'fa-check' : 'fa-shipping-fast'}"></i>
                            </div>
                            <div class="media-body">
                                <h6 class="mt-0 mb-1">Out for Delivery</h6>
                                <small class="text-muted">Your order is on the way</small>
                            </div>
                        </li>
                        
                        <li class="media tracking-step">
                            <div class="tracking-icon ${order.orderStatus eq 'DELIVERED' ? 'active' : ''} mr-3">
                                <i class="fas ${order.orderStatus eq 'DELIVERED' ? 'fa-check' : 'fa-home'}"></i>
                            </div>
                            <div class="media-body">
                                <h6 class="mt-0 mb-1">Delivered</h6>
                                <c:if test="${order.deliveredAt != null}">
                                    <small class="text-muted">
                                        <fmt:formatDate value="${order.deliveredAt}" pattern="yyyy-MM-dd HH:mm" />
                                    </small>
                                </c:if>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
        
        <h5 class="mt-4">Items</h5>
        <div class="table-responsive">
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>Item</th>
                        <th>Quantity</th>
                        <th>Price</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${order.orderItems}" var="item">
                        <tr>
                            <td>
                                <strong>${item.pizza.name}</strong>
                                <c:if test="${not empty item.toppings}">
                                    <small class="d-block text-muted">
                                        Extra toppings: 
                                        <c:forEach items="${item.toppings}" var="topping" varStatus="status">
                                            ${topping.name}${!status.last ? ', ' : ''}
                                        </c:forEach>
                                    </small>
                                </c:if>
                            </td>
                            <td>${item.quantity}</td>
                            <td>$<fmt:formatNumber value="${item.price}" type="currency" currencySymbol="" /></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<div>
    <a href="${pageContext.request.contextPath}/orders" class="btn btn-outline-secondary">
        <i class="fas fa-arrow-left"></i> Back to Orders
    </a>
</div>

<jsp:include page="../common/footer.jsp" />