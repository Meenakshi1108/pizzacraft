<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Manage Orders" />
</jsp:include>

<div class="mb-3">
    <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary">
        <i class="fas fa-arrow-left"></i> Back to Dashboard
    </a>
</div>

<div class="card">
    <div class="card-header">All Orders</div>
    <div class="card-body">
        <c:choose>
            <c:when test="${empty orders}">
                <p class="text-center my-3">No orders found.</p>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Order #</th>
                                <th>Date</th>
                                <th>Customer</th>
                                <th>Status</th>
                                <th>Total</th>
                                <th>Delivery Person</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${orders}" var="order">
                                <tr>
                                    <td>${order.id}</td>
                                    <td><fmt:formatDate value="${order.createdAt}" pattern="yyyy-MM-dd HH:mm" /></td>
                                    <td>${order.userId}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${order.assignedToUserId != null && (order.orderStatus eq 'READY' || order.orderStatus eq 'OUT_FOR_DELIVERY')}">
                                                <span class="badge 
                                                    ${order.orderStatus eq 'READY' ? 'badge-warning' :
                                                     order.orderStatus eq 'OUT_FOR_DELIVERY' ? 'badge-dark' : ''}">
                                                    ${order.orderStatus}
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <form action="${pageContext.request.contextPath}/admin/orders" method="post">
                                                    <input type="hidden" name="action" value="update-status">
                                                    <input type="hidden" name="orderId" value="${order.id}">
                                                    <select name="status" class="form-control form-control-sm" 
                                                            onchange="this.form.submit()"
                                                            ${order.orderStatus eq 'DELIVERED' || order.orderStatus eq 'CANCELLED' ? 'disabled' : ''}>
                                                        <option value="PLACED" ${order.orderStatus eq 'PLACED' ? 'selected' : ''}>PLACED</option>
                                                        <option value="PREPARING" ${order.orderStatus eq 'PREPARING' ? 'selected' : ''}>PREPARING</option>
                                                        <option value="READY" ${order.orderStatus eq 'READY' ? 'selected' : ''}>READY</option>
                                                        <option value="CANCELLED" ${order.orderStatus eq 'CANCELLED' ? 'selected' : ''}>CANCELLED</option>
                                                        <c:if test="${order.orderStatus eq 'DELIVERED'}">
                                                            <option value="DELIVERED" selected>DELIVERED</option>
                                                        </c:if>
                                                    </select>
                                                </form>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>$<fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="" /></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${order.assignedToUserId != null}">
                                                Assigned to ID: ${order.assignedToUserId}
                                            </c:when>
                                            <c:when test="${order.orderStatus eq 'READY' && not empty deliveryPeople}">
                                                <form action="${pageContext.request.contextPath}/admin/orders" method="post">
                                                    <input type="hidden" name="action" value="assign">
                                                    <input type="hidden" name="orderId" value="${order.id}">
                                                    <div class="input-group input-group-sm">
                                                        <select name="deliveryPersonId" class="form-control form-control-sm">
                                                            <option value="">Select...</option>
                                                            <c:forEach items="${deliveryPeople}" var="deliveryPerson">
                                                                <option value="${deliveryPerson.id}">${deliveryPerson.fullName}</option>
                                                            </c:forEach>
                                                        </select>
                                                        <div class="input-group-append">
                                                            <button type="submit" class="btn btn-sm btn-primary">Assign</button>
                                                        </div>
                                                    </div>
                                                </form>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Not assigned</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/orders/${order.id}" class="btn btn-sm btn-outline-primary">
                                            <i class="fas fa-eye"></i> Details
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

<jsp:include page="../common/footer.jsp" />