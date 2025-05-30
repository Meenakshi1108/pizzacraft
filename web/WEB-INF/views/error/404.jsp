<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Page Not Found" />
</jsp:include>

<div class="text-center py-5">
    <div class="mb-4">
        <i class="fas fa-exclamation-circle fa-5x text-warning"></i>
    </div>
    <h1>404</h1>
    <h3>Page Not Found</h3>
    <p class="lead">The page you are looking for does not exist or has been moved.</p>
    <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Go to Homepage</a>
</div>

<jsp:include page="../common/footer.jsp" />