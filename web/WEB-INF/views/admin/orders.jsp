<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Manage Orders" />
</jsp:include>

<div class="container mt-4">
    <h1 class="mb-4">Manage Orders</h1>
    
    <!-- Alert Messages -->
    <c:if test="${not empty sessionScope.success}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle me-2"></i> ${sessionScope.success}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="success" scope="session" />
    </c:if>
    
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-circle me-2"></i> ${sessionScope.error}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="error" scope="session" />
    </c:if>    <!-- Debug Information for Troubleshooting -->
    <c:if test="${pageContext.request.isUserInRole('ADMIN')}">
        <div class="alert alert-secondary mt-2 mb-4">
            <h5>Debug Information:</h5>
            <p>Number of orders: ${orders != null ? orders.size() : 'null'}</p>
            <p>Number of delivery persons: ${deliveryPersons != null ? deliveryPersons.size() : 'null'}</p>
            <p>Current user: ${sessionScope.user.username} (${sessionScope.user.role})</p>
            <p>Admin Links: 
                <a href="${pageContext.request.contextPath}/admin/diagnostic" class="btn btn-sm btn-outline-secondary">System Diagnostic</a>
                <a href="${pageContext.request.contextPath}/admin/db-check" class="btn btn-sm btn-outline-info">Database Check</a>
            </p>
        </div>
    </c:if>
    
    <c:choose>
        <c:when test="${orders == null}">
            <div class="alert alert-warning">
                <i class="fas fa-exclamation-circle"></i>
                Error loading orders. Please try refreshing the page.
            </div>
        </c:when>
        <c:when test="${empty orders}">
            <div class="alert alert-info">No orders found.</div>
        </c:when>
        <c:otherwise>
            <div class="table-responsive">
                <table class="table table-striped table-hover">
                    <thead class="table-dark">
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
                                <td>
                                    <c:if test="${order.createdAt != null}">
                                        <fmt:formatDate value="${order.createdAt}" pattern="yyyy-MM-dd HH:mm" />
                                    </c:if>
                                    <c:if test="${order.createdAt == null}">
                                        <span class="text-muted">Unknown</span>
                                    </c:if>
                                </td>
                                <td>${order.userId}</td>
                                <td>
                                    <span class="badge 
                                        ${order.orderStatus eq 'PLACED' ? 'bg-info' : 
                                         order.orderStatus eq 'PREPARING' ? 'bg-primary' :
                                         order.orderStatus eq 'READY' ? 'bg-warning' :
                                         order.orderStatus eq 'OUT_FOR_DELIVERY' ? 'bg-secondary' :
                                         order.orderStatus eq 'DELIVERED' ? 'bg-success' :
                                         'bg-danger'}">
                                        ${order.orderStatus}
                                    </span>
                                </td>
                                <td>₹<fmt:formatNumber value="${order.totalAmount}" type="number" minFractionDigits="2" maxFractionDigits="2" /></td>                                <td>
                                    <c:choose>
                                        <c:when test="${empty order.deliveryPersonId}">
                                            <span class="text-muted">Not assigned</span>
                                        </c:when>
                                        <c:when test="${empty order.deliveryPersonName}">
                                            <span class="text-muted">Delivery Person #${order.deliveryPersonId}</span>
                                        </c:when>
                                        <c:otherwise>
                                            ${order.deliveryPersonName}
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <a href="${pageContext.request.contextPath}/admin/view-order/${order.id}" 
                                           class="btn btn-sm btn-primary">
                                            <i class="fas fa-eye"></i>
                                        </a>
                                        
                                        <!-- Status Update Button -->
                                        <button type="button" class="btn btn-sm btn-outline-secondary"
                                                data-bs-toggle="modal" data-bs-target="#updateStatusModal${order.id}">
                                            <i class="fas fa-sync-alt"></i>
                                        </button>
                                        
                                        <!-- Assign Delivery Person Button -->
                                        <button type="button" class="btn btn-sm btn-outline-success"
                                                data-bs-toggle="modal" data-bs-target="#assignDeliveryModal${order.id}">
                                            <i class="fas fa-user-tag"></i>
                                        </button>
                                    </div>
                                    
                                    <!-- Update Status Modal -->
                                    <div class="modal fade" id="updateStatusModal${order.id}" tabindex="-1" 
                                         aria-labelledby="updateStatusModalLabel${order.id}" aria-hidden="true">
                                        <div class="modal-dialog">
                                            <!-- Modal content -->
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <h5 class="modal-title" id="updateStatusModalLabel${order.id}">Update Order Status</h5>
                                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                </div>
                                                <form action="${pageContext.request.contextPath}/admin/orders" method="post">
                                                    <div class="modal-body">
                                                        <input type="hidden" name="orderId" value="${order.id}">
                                                        <input type="hidden" name="action" value="update-status">
                                                        <div class="mb-3">
                                                            <label for="status${order.id}" class="form-label">Status</label>
                                                            <select class="form-select" id="status${order.id}" name="status" required>
                                                                <option value="PLACED" ${order.orderStatus eq 'PLACED' ? 'selected' : ''}>Placed</option>
                                                                <option value="PREPARING" ${order.orderStatus eq 'PREPARING' ? 'selected' : ''}>Preparing</option>
                                                                <option value="READY" ${order.orderStatus eq 'READY' ? 'selected' : ''}>Ready</option>
                                                                <option value="OUT_FOR_DELIVERY" ${order.orderStatus eq 'OUT_FOR_DELIVERY' ? 'selected' : ''}>Out for Delivery</option>
                                                                <option value="DELIVERED" ${order.orderStatus eq 'DELIVERED' ? 'selected' : ''}>Delivered</option>
                                                                <option value="CANCELLED" ${order.orderStatus eq 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                                                            </select>
                                                        </div>
                                                    </div>
                                                    <div class="modal-footer">
                                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                                        <button type="submit" class="btn btn-primary">Save Changes</button>
                                                    </div>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <!-- Assign Delivery Person Modal -->
                                    <div class="modal fade" id="assignDeliveryModal${order.id}" tabindex="-1" 
                                         aria-labelledby="assignDeliveryModalLabel${order.id}" aria-hidden="true">
                                        <div class="modal-dialog">
                                            <!-- Modal content -->
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <h5 class="modal-title" id="assignDeliveryModalLabel${order.id}">Assign Delivery Person</h5>
                                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                </div>                                                <form action="${pageContext.request.contextPath}/admin/orders" method="post">
                                                    <div class="modal-body">
                                                        <input type="hidden" name="orderId" value="${order.id}">
                                                        <input type="hidden" name="action" value="assign">
                                                        <div class="mb-3">                                                            <label for="deliveryPersonId${order.id}" class="form-label">Select Delivery Person</label>
                                                            <select class="form-select" id="deliveryPersonId${order.id}" name="deliveryPersonId" required>                                                <option value="">Select a delivery person</option>
                                                <c:choose>
                                                    <c:when test="${deliveryPersons == null}">
                                                        <option value="" disabled>Error loading delivery persons</option>
                                                    </c:when>
                                                    <c:when test="${empty deliveryPersons}">
                                                        <option value="" disabled>No delivery persons available</option>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:forEach items="${deliveryPersons}" var="deliveryPerson">
                                                            <c:choose>
                                                                <c:when test="${deliveryPerson.id < 0}">
                                                                    <!-- This is a dummy user -->
                                                                    <option value="" disabled>${deliveryPerson.fullName}</option>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <option value="${deliveryPerson.id}" 
                                                                            ${order.deliveryPersonId eq deliveryPerson.id ? 'selected' : ''}>
                                                                        ${not empty deliveryPerson.fullName ? deliveryPerson.fullName : 'Delivery Person #'}${deliveryPerson.id}
                                                                        <c:if test="${not empty deliveryPerson.activeDeliveries && deliveryPerson.activeDeliveries > 0}">
                                                                            (${deliveryPerson.activeDeliveries} active)
                                                                        </c:if>
                                                                    </option>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:forEach>
                                                    </c:otherwise>
                                                </c:choose>
                                                            </select>
                                                        </div>
                                                    </div>
                                                    <div class="modal-footer">
                                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                                        <button type="submit" class="btn btn-primary">Assign</button>
                                                    </div>
                                                </form>
                                            </div>
                                        </div>
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

<jsp:include page="../common/footer.jsp" />