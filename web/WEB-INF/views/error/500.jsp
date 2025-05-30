<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Server Error" />
</jsp:include>

<div class="text-center py-5">
    <div class="mb-4">
        <i class="fas fa-exclamation-triangle fa-5x text-danger"></i>
    </div>
    <h1>500</h1>
    <h3>Server Error</h3>
    <p class="lead">Something went wrong on our end. We're working to fix it.</p>
    <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Go to Homepage</a>
</div>

<jsp:include page="../common/footer.jsp" />