<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%-- 
  Order Details View - Shows detailed information about a specific order
  Includes order items, delivery details, pricing, and order status
--%>
<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Order Details #${order.id}" />
</jsp:include>

<div class="container py-4">
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/">Home</a></li>
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/orders">My Orders</a></li>
            <li class="breadcrumb-item active" aria-current="page">Order #${order.id}</li>
        </ol>
    </nav>
    
    <div class="row">
        <div class="col-lg-8">
            <div class="card shadow-sm mb-4">
                <div class="card-header bg-white">
                    <div class="d-flex justify-content-between align-items-center">                        <h5 class="mb-0">
                            <i class="fas fa-box me-2"></i> Order #${order.id}
                        </h5>
                        <span class="badge bg-${order.orderStatus eq 'DELIVERED' ? 'success' : 
                                              order.orderStatus eq 'CANCELLED' ? 'danger' : 
                                              'primary'} fs-6">
                            ${order.orderStatus}
                        </span>
                    </div>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <h6><i class="fas fa-calendar me-2"></i> Order Date:</h6>
                            <p><fmt:formatDate value="${order.createdAt}" pattern="dd MMM yyyy, hh:mm a" /></p>
                        </div>
                        <div class="col-md-6 mb-3">
                            <h6><i class="fas fa-map-marker-alt me-2"></i> Delivery Address:</h6>
                            <p>${order.deliveryAddress}</p>
                        </div>
                        <div class="col-md-6 mb-3">
                            <h6><i class="fas fa-phone me-2"></i> Contact Number:</h6>
                            <p>${order.contactNumber}</p>
                        </div>
                        <div class="col-md-6 mb-3">
                            <h6><i class="fas fa-motorcycle me-2"></i> Delivery Person:</h6>
                            <p>${not empty order.deliveryPersonName ? order.deliveryPersonName : 'Not assigned yet'}</p>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="card shadow-sm mb-4">
                <div class="card-header bg-white">
                    <h5 class="mb-0">
                        <i class="fas fa-utensils me-2"></i> Order Items
                    </h5>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table align-middle">
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
                                            <h6 class="mb-1">${item.pizza.name}</h6>
                                            <c:if test="${not empty item.toppings and item.toppings.size() > 0}">
                                                <small class="text-muted">
                                                    Extra toppings: 
                                                    <c:forEach items="${item.toppings}" var="topping" varStatus="toppingStatus">
                                                        ${topping.name}<c:if test="${!toppingStatus.last}">, </c:if>
                                                    </c:forEach>
                                                </small>
                                            </c:if>
                                        </td>
                                        <td>${item.quantity}x</td>
                                        <td class="text-end">
                                            ₹<fmt:formatNumber value="${item.price}" type="number" 
                                                minFractionDigits="2" maxFractionDigits="2" />
                                        </td>
                                    </tr>
                                </c:forEach>
                                <tr class="table-light fw-bold">
                                    <td colspan="2" class="text-end">Total:</td>
                                    <td class="text-end">
                                        ₹<fmt:formatNumber value="${order.totalAmount}" type="number" 
                                                minFractionDigits="2" maxFractionDigits="2" />
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="col-lg-4">
            <div class="card shadow-sm mb-4">
                <div class="card-header bg-white">
                    <h5 class="mb-0">
                        <i class="fas fa-truck me-2"></i> Order Status
                    </h5>
                </div>
                <div class="card-body">
                    <div class="order-timeline">
                        <div class="timeline-container">
                            <div class="timeline-track">
                                <c:choose>
                                    <c:when test="${order.orderStatus eq 'CANCELLED'}">
                                        <div class="timeline-progress bg-danger" style="width: 100%"></div>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="progress" value="${
                                            order.orderStatus eq 'PLACED' ? 20 :
                                            order.orderStatus eq 'PREPARING' ? 40 :
                                            order.orderStatus eq 'READY' ? 60 :
                                            order.orderStatus eq 'OUT_FOR_DELIVERY' ? 80 :
                                            order.orderStatus eq 'DELIVERED' ? 100 : 0
                                        }" />
                                        <div class="timeline-progress" style="width:${progress}%"></div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            
                            <div class="timeline-points">
                                <c:set var="statuses" value="${['PLACED', 'PREPARING', 'READY', 'OUT_FOR_DELIVERY', 'DELIVERED']}" />
                                
                                <c:forEach items="${statuses}" var="status" varStatus="loop">
                                    <c:choose>
                                        <c:when test="${order.orderStatus eq 'CANCELLED' && loop.index == 0}">
                                            <div class="point active">
                                                <div class="point-icon">
                                                    <i class="fas fa-times"></i>
                                                </div>
                                                <div class="point-label">Cancelled</div>
                                            </div>
                                        </c:when>
                                        <c:when test="${order.orderStatus eq 'CANCELLED' && loop.index > 0}">
                                            <div class="point">
                                                <div class="point-icon">
                                                    <i class="fas fa-circle"></i>
                                                </div>
                                                <div class="point-label">${status}</div>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <c:set var="isActive" value="${
                                                (status eq 'PLACED' && (order.orderStatus eq 'PLACED' || order.orderStatus eq 'PREPARING' || order.orderStatus eq 'READY' || order.orderStatus eq 'OUT_FOR_DELIVERY' || order.orderStatus eq 'DELIVERED')) ||
                                                (status eq 'PREPARING' && (order.orderStatus eq 'PREPARING' || order.orderStatus eq 'READY' || order.orderStatus eq 'OUT_FOR_DELIVERY' || order.orderStatus eq 'DELIVERED')) ||
                                                (status eq 'READY' && (order.orderStatus eq 'READY' || order.orderStatus eq 'OUT_FOR_DELIVERY' || order.orderStatus eq 'DELIVERED')) ||
                                                (status eq 'OUT_FOR_DELIVERY' && (order.orderStatus eq 'OUT_FOR_DELIVERY' || order.orderStatus eq 'DELIVERED')) ||
                                                (status eq 'DELIVERED' && order.orderStatus eq 'DELIVERED')
                                            }" />
                                            
                                            <div class="point ${isActive ? 'active' : ''}">
                                                <div class="point-icon">
                                                    <i class="${
                                                        status eq 'PLACED' ? 'fas fa-clipboard-list' : 
                                                        status eq 'PREPARING' ? 'fas fa-utensils' :
                                                        status eq 'READY' ? 'fas fa-box' :
                                                        status eq 'OUT_FOR_DELIVERY' ? 'fas fa-truck' :
                                                        status eq 'DELIVERED' ? 'fas fa-check-circle' : 'fas fa-circle'
                                                    }"></i>
                                                </div>
                                                <div class="point-label">${status}</div>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </div>
                        </div>
                          <c:if test="${order.orderStatus eq 'CANCELLED'}">
                            <div class="cancelled-overlay">
                                <div class="cancelled-message">
                                    <i class="fas fa-ban me-2"></i> This order has been cancelled
                                </div>
                            </div>
                        </c:if>
                    </div>
                    
                    <div class="mt-4 text-center">
                        <c:if test="${order.orderStatus ne 'DELIVERED' && order.orderStatus ne 'CANCELLED'}">
                            <p class="text-muted mb-0">Estimated delivery time:</p>
                            <p class="fs-5 fw-bold">30-45 minutes</p>
                        </c:if>
                    </div>
                </div>
            </div>
            
            <div class="card shadow-sm">
                <div class="card-header bg-white">
                    <h5 class="mb-0">
                        <i class="fas fa-info-circle me-2"></i> Need Help?
                    </h5>
                </div>
                <div class="card-body">
                    <p>Having issues with your order?</p>
                    <a href="#" class="btn btn-outline-primary w-100 mb-2">
                        <i class="fas fa-headset me-2"></i> Contact Support
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Add the CSS for the timeline -->
<style>
.order-timeline {
    position: relative;
    margin: 30px 0;
    padding: 0;
}

.timeline-container {
    position: relative;
    padding: 20px 0;
}

.timeline-track {
    position: absolute;
    top: 50px;
    left: 0;
    right: 0;
    height: 6px;
    background-color: #e9ecef;
    border-radius: 3px;
    z-index: 1;
}

.timeline-progress {
    position: absolute;
    top: 0;
    left: 0;
    height: 100%;
    background-color: #28a745;
    border-radius: 3px;
    transition: width 0.5s ease;
}

.timeline-points {
    display: flex;
    justify-content: space-between;
    position: relative;
    z-index: 2;
}

.point {
    display: flex;
    flex-direction: column;
    align-items: center;
    flex: 1;
}

.point-icon {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background-color: #fff;
    border: 3px solid #e9ecef;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 18px;
    color: #6c757d;
    margin-bottom: 8px;
}

.point.active .point-icon {
    border-color: #28a745;
    background-color: #28a745;
    color: #fff;
}

.point-label {
    font-size: 12px;
    font-weight: 500;
    color: #6c757d;
    text-align: center;
}

.point.active .point-label {
    color: #212529;
    font-weight: 700;
}

.cancelled-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: rgba(255, 255, 255, 0.7);
    z-index: 3;
}

.cancelled-message {
    background-color: #dc3545;
    color: white;
    padding: 8px 16px;
    border-radius: 4px;
    font-weight: 600;
}

.cancelled-message i {
    animation: pulse 1.5s infinite;
}

/* Add animation */
@keyframes pulse {
    0% { opacity: 0.5; }
    50% { opacity: 1; }
    100% { opacity: 0.5; }
}

.point.active:last-child .point-icon {
    animation: pulse 1.5s infinite;
}
</style>

<jsp:include page="../common/footer.jsp" />