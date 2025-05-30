<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Admin Dashboard" />
</jsp:include>

<div class="row mb-4">
    <div class="col-md-12">
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Admin Controls</h5>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-4 mb-3">
                        <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-outline-primary btn-block">
                            <i class="fas fa-clipboard-list"></i> Manage Orders
                        </a>
                    </div>
                    <div class="col-md-4 mb-3">
                        <a href="${pageContext.request.contextPath}/admin/pizzas" class="btn btn-outline-success btn-block">
                            <i class="fas fa-pizza-slice"></i> Manage Menu
                        </a>
                    </div>
                    <div class="col-md-4 mb-3">
                        <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-outline-info btn-block">
                            <i class="fas fa-users"></i> Manage Users
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="row">
    <div class="col-md-12">
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <h5 class="mb-0">Recent Orders</h5>
                <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-sm btn-primary">View All</a>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty recentOrders}">
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
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${recentOrders}" var="order" end="9">
                                        <tr>
                                            <td>${order.id}</td>
                                            <td><fmt:formatDate value="${order.createdAt}" pattern="yyyy-MM-dd HH:mm" /></td>
                                            <td>${order.userId}</td>
                                            <td>
                                                <span class="badge 
                                                    ${order.orderStatus eq 'PLACED' ? 'badge-info' : 
                                                     order.orderStatus eq 'PREPARING' ? 'badge-primary' :
                                                     order.orderStatus eq 'READY' ? 'badge-warning' :
                                                     order.orderStatus eq 'OUT_FOR_DELIVERY' ? 'badge-dark' :
                                                     order.orderStatus eq 'DELIVERED' ? 'badge-success' :
                                                     'badge-danger'}">
                                                    ${order.orderStatus}
                                                </span>
                                            </td>
                                            <td>$<fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="" /></td>
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
    </div>
</div>

<jsp:include page="../common/footer.jsp" />