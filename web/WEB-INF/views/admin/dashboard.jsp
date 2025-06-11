<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Admin Dashboard" />
</jsp:include>

<!-- Stats Overview Cards -->
<div class="row mb-4">
    <div class="col-xl-3 col-md-6 mb-4">
        <div class="card border-left-primary shadow h-100 py-2">
            <div class="card-body">
                <div class="row no-gutters align-items-center">
                    <div class="col mr-2">
                        <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">
                            Total Revenue</div>
                        <div class="h5 mb-0 font-weight-bold text-gray-800">
                            ₹<fmt:formatNumber value="${stats.totalRevenue}" type="number" minFractionDigits="2" maxFractionDigits="2" />
                        </div>
                    </div>
                    <div class="col-auto">
                        <i class="fas fa-rupee-sign fa-2x text-gray-300"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="col-xl-3 col-md-6 mb-4">
        <div class="card border-left-success shadow h-100 py-2">
            <div class="card-body">
                <div class="row no-gutters align-items-center">
                    <div class="col mr-2">
                        <div class="text-xs font-weight-bold text-success text-uppercase mb-1">
                            Today's Revenue</div>
                        <div class="h5 mb-0 font-weight-bold text-gray-800">
                            ₹<fmt:formatNumber value="${stats.todayRevenue}" type="number" minFractionDigits="2" maxFractionDigits="2" />
                        </div>
                    </div>
                    <div class="col-auto">
                        <i class="fas fa-calendar-day fa-2x text-gray-300"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="col-xl-3 col-md-6 mb-4">
        <div class="card border-left-info shadow h-100 py-2">
            <div class="card-body">
                <div class="row no-gutters align-items-center">
                    <div class="col mr-2">
                        <div class="text-xs font-weight-bold text-info text-uppercase mb-1">
                            Total Orders</div>
                        <div class="h5 mb-0 font-weight-bold text-gray-800">${stats.totalOrders}</div>
                    </div>
                    <div class="col-auto">
                        <i class="fas fa-clipboard-list fa-2x text-gray-300"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="col-xl-3 col-md-6 mb-4">
        <div class="card border-left-warning shadow h-100 py-2">
            <div class="card-body">
                <div class="row no-gutters align-items-center">
                    <div class="col mr-2">
                        <div class="text-xs font-weight-bold text-warning text-uppercase mb-1">
                            Active Deliveries</div>
                        <div class="h5 mb-0 font-weight-bold text-gray-800">${stats.activeDeliveries}</div>
                    </div>
                    <div class="col-auto">
                        <i class="fas fa-motorcycle fa-2x text-gray-300"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Admin Controls -->
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

<!-- Detailed Statistics -->
<div class="row">
    <!-- Order Stats -->
    <div class="col-md-4 mb-4">
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Order Statistics</h5>
            </div>
            <div class="card-body">
                <ul class="list-group">
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        Pending Orders
                        <span class="badge badge-info badge-pill">${stats.pendingOrders}</span>
                    </li>
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        Being Prepared
                        <span class="badge badge-primary badge-pill">${stats.preparingOrders}</span>
                    </li>
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        Ready for Pickup
                        <span class="badge badge-warning badge-pill">${stats.readyOrders}</span>
                    </li>
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        Out for Delivery
                        <span class="badge badge-dark badge-pill">${stats.activeDeliveries}</span>
                    </li>
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        Delivered
                        <span class="badge badge-success badge-pill">${stats.completedOrders}</span>
                    </li>
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        Cancelled
                        <span class="badge badge-danger badge-pill">${stats.cancelledOrders}</span>
                    </li>
                </ul>
            </div>
        </div>
    </div>
    
    <!-- Menu Stats -->
    <div class="col-md-4 mb-4">
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">Menu Statistics</h5>
            </div>
            <div class="card-body">
                <ul class="list-group">                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        Total Menu Items
                        <span class="badge badge-primary badge-pill">${stats.totalMenuItems != null ? stats.totalMenuItems : 0}</span>
                    </li>
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        Available Pizzas
                        <span class="badge badge-success badge-pill">${stats.availablePizzas != null ? stats.availablePizzas : 0}</span>
                    </li>
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        Out of Stock
                        <span class="badge badge-danger badge-pill">${stats.outOfStockPizzas != null ? stats.outOfStockPizzas : 0}</span>
                    </li>
                </ul>
            </div>
        </div>
    </div>
    
    <!-- User Stats -->
    <div class="col-md-4 mb-4">
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">User Statistics</h5>
            </div>
            <div class="card-body">
                <ul class="list-group">                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        Total Users
                        <span class="badge badge-primary badge-pill">${stats.totalUsers != null ? stats.totalUsers : 0}</span>
                    </li>
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        Customers
                        <span class="badge badge-success badge-pill">${stats.totalCustomers != null ? stats.totalCustomers : 0}</span>
                    </li>
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        Delivery Personnel
                        <span class="badge badge-info badge-pill">${stats.totalDeliveryPersons != null ? stats.totalDeliveryPersons : 0}</span>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</div>

<!-- Recent Orders Table -->
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
                                    <c:forEach items="${recentOrders}" var="order">
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
                                            <td>
                                                ₹<fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="" />
                                            </td>                                            <td>
                                                <a href="${pageContext.request.contextPath}/view-order/${order.id}" class="btn btn-sm btn-outline-primary">
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