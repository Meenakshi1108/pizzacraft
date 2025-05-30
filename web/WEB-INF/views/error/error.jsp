<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Error" />
</jsp:include>

<div class="text-center py-5">
    <div class="mb-4">
        <i class="fas fa-exclamation-triangle fa-5x text-danger"></i>
    </div>
    <h3>An Error Occurred</h3>
    <p class="lead">${error || "Something went wrong. Please try again later."}</p>
    <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Go to Homepage</a>
</div>

<jsp:include page="../common/footer.jsp" />