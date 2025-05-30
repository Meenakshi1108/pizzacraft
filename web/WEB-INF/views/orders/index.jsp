<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="My Orders" />
</jsp:include>

<c:choose>
    <c:when test="${empty orders}">
        <div class="text-center py-5">
            <div class="mb-4">
                <i class="fas fa-clipboard-list fa-5x text-muted"></i>
            </div>
            <h3>No orders yet</h3>
            <p class="lead">You haven't placed any orders yet.</p>
            <a href="${pageContext.request.contextPath}/menu" class="btn btn-primary">Order Now</a>
        </div>
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
                        <th>Total</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${orders}" var="order">
                        <tr>
                            <td>${order.id}</td>
                            <td><fmt:formatDate value="${order.createdAt}" pattern="yyyy-MM-dd HH:mm" /></td>
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
                            <td>${order.orderItems.size()}</td>
                            <td>₹<fmt:formatNumber value="${order.totalAmount}" type="number" minFractionDigits="2" maxFractionDigits="2" /></td>
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

<jsp:include page="../common/footer.jsp" />