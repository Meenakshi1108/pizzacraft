<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Error" />
</jsp:include>

<div class="text-center py-5">
    <div class="mb-4">
        <i class="fas fa-exclamation-triangle fa-5x text-danger"></i>
    </div>    <h3>An Error Occurred</h3>
    <p class="lead">
        <c:choose>
            <c:when test="${not empty error}">
                ${error}
            </c:when>
            <c:otherwise>
                Something went wrong. Please try again later.
            </c:otherwise>
        </c:choose>
    </p>
    
    <c:if test="${not empty errorDetail}">
        <div class="alert alert-danger text-left" style="max-width: 800px; margin: 0 auto;">
            <h5>Error Details:</h5>
            <pre style="white-space: pre-wrap; word-break: break-all;">${errorDetail}</pre>
        </div>
    </c:if>
    
    <div class="mt-4">
        <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Go to Homepage</a>
        <a href="javascript:history.back()" class="btn btn-secondary ms-2">Go Back</a>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />