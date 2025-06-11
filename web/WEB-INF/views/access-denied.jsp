<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="common/header.jsp">
    <jsp:param name="title" value="Access Denied" />
</jsp:include>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-8 text-center">
            <div class="mb-4">
                <i class="fas fa-lock fa-5x text-danger"></i>
            </div>
            <h1 class="display-4 mb-3">Access Denied</h1>
            
            <c:if test="${not empty sessionScope.flashMessage}">
                <div class="alert alert-${sessionScope.flashType} mb-4">
                    <p class="mb-0">${sessionScope.flashMessage}</p>
                </div>
                <c:remove var="flashMessage" scope="session" />
                <c:remove var="flashType" scope="session" />
            </c:if>
            
            <div class="card shadow mb-4">
                <div class="card-body py-5">
                    <p class="lead">Sorry, you don't have permission to access this page.</p>
                    <p class="text-muted">This might be because:</p>
                    
                    <ul class="list-unstyled text-muted text-start mx-auto" style="max-width: 400px;">
                        <li><i class="fas fa-check-circle text-danger me-2"></i> You need to be logged in</li>
                        <li><i class="fas fa-check-circle text-danger me-2"></i> Your account doesn't have the required role</li>
                        <li><i class="fas fa-check-circle text-danger me-2"></i> The resource belongs to another user</li>
                    </ul>
                </div>
            </div>
            
            <div class="mt-4">
                <a href="${pageContext.request.contextPath}/" class="btn btn-primary me-2">
                    <i class="fas fa-home me-2"></i>Go to Homepage
                </a>
                
                <c:if test="${empty sessionScope.user}">
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-primary">
                        <i class="fas fa-sign-in-alt me-2"></i>Login
                    </a>
                </c:if>
            </div>
        </div>
    </div>
</div>

<jsp:include page="common/footer.jsp" />
