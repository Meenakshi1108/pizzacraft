<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Delivery Dashboard" />
</jsp:include>

<div class="row mb-4">
    <div class="col-md-12">
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Active Deliveries</h5>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty activeOrders}">
                        <div class="text-center my-5">
                            <div class="mb-3">
                                <i class="fas fa-motorcycle fa-4x text-muted"></i>
                            </div>
                            <h4 class="text-muted">No active deliveries</h4>
                            <p>You don't have any orders to deliver at the moment.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-striped">
                                <thead>
                                    <tr>
                                        <th>Order #</th>
                                        <th>Time</th>
                                        <th>Status</th>
                                        <th>Address</th>
                                        <th>Items</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${activeOrders}" var="order">
                                        <tr>
                                            <td>${order.id}</td>
                                            <td><fmt:formatDate value="${order.assignedAt}" pattern="HH:mm" /></td>
                                            <td>
                                                <span class="badge 
                                                    ${order.orderStatus eq 'READY' ? 'badge-warning' :
                                                     order.orderStatus eq 'OUT_FOR_DELIVERY' ? 'badge-dark' : 'badge-info'}">
                                                    ${order.orderStatus}
                                                </span>
                                            </td>
                                            <td>${order.deliveryAddress}</td>
                                            <td>${order.orderItems.size()} items</td>                                            <td>
                                                <div class="btn-group">
                                                    <a href="${pageContext.request.contextPath}/view-order/${order.id}" 
                                                       class="btn btn-sm btn-outline-secondary">
                                                        <i class="fas fa-info-circle"></i> Details
                                                    </a>
                                                    
                                                    <c:choose>
                                                        <c:when test="${order.orderStatus eq 'READY'}">
                                                            <form action="${pageContext.request.contextPath}/delivery/update-order" method="post">
                                                                <input type="hidden" name="orderId" value="${order.id}">
                                                                <input type="hidden" name="action" value="pickup">
                                                                <button type="submit" class="btn btn-sm btn-primary ml-2">
                                                                    <i class="fas fa-motorcycle"></i> Pick Up
                                                                </button>
                                                            </form>
                                                        </c:when>
                                                        <c:when test="${order.orderStatus eq 'OUT_FOR_DELIVERY'}">
                                                            <form action="${pageContext.request.contextPath}/delivery/update-order" method="post">
                                                                <input type="hidden" name="orderId" value="${order.id}">
                                                                <input type="hidden" name="action" value="deliver">
                                                                <button type="submit" class="btn btn-sm btn-success ml-2">
                                                                    <i class="fas fa-check"></i> Mark as Delivered
                                                                </button>
                                                            </form>
                                                        </c:when>
                                                    </c:choose>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<div class="row">
    <div class="col-md-12">
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Delivery History</h5>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty completedOrders}">
                        <p class="text-center text-muted">No delivery history yet.</p>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-striped">
                                <thead>
                                    <tr>
                                        <th>Order #</th>
                                        <th>Date</th>
                                        <th>Status</th>
                                        <th>Items</th>
                                        <th>Address</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${completedOrders}" var="order">
                                        <tr>
                                            <td>${order.id}</td>
                                            <td><fmt:formatDate value="${order.deliveredAt}" pattern="yyyy-MM-dd HH:mm" /></td>
                                            <td>
                                                <span class="badge 
                                                    ${order.orderStatus eq 'DELIVERED' ? 'badge-success' : 'badge-danger'}">
                                                    ${order.orderStatus}
                                                </span>
                                            </td>
                                            <td>${order.orderItems.size()} items</td>
                                            <td>${order.deliveryAddress}</td>                                            <td>
                                                <a href="${pageContext.request.contextPath}/view-order/${order.id}" 
                                                   class="btn btn-sm btn-outline-secondary">
                                                    <i class="fas fa-info-circle"></i> Details
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />