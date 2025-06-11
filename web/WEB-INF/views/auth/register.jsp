<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Register" />
</jsp:include>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card shadow">
                <div class="card-header bg-primary text-white">
                    <h4 class="mb-0">Create an Account</h4>
                </div>
                <div class="card-body">
                    <!-- Alert Messages -->
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="fas fa-exclamation-circle me-2"></i> ${error}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>
                    <c:if test="${not empty success}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="fas fa-check-circle me-2"></i> ${success}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>
                    
                    <form action="${pageContext.request.contextPath}/register" method="post" id="registerForm">
                        <div class="mb-3">
                            <label for="username" class="form-label">Username *</label>
                            <input type="text" class="form-control" id="username" name="username" required
                                   value="${username}" placeholder="Choose a username">
                        </div>
                        <div class="mb-3">
                            <label for="email" class="form-label">Email *</label>
                            <input type="email" class="form-control" id="email" name="email" required
                                   value="${email}" placeholder="Your email address">
                        </div>
                        <div class="mb-3">
                            <label for="password" class="form-label">Password *</label>
                            <!-- Updated with minlength attribute -->
                            <input type="password" class="form-control" id="password" name="password" required
                                   minlength="8" placeholder="Create a password">
                            <div class="form-text text-muted">
                                Password must be at least 8 characters long
                            </div>
                        </div>
                        <div class="mb-3">
                            <label for="confirmPassword" class="form-label">Confirm Password *</label>
                            <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required
                                   minlength="8" placeholder="Confirm your password">
                        </div>
                        <div class="mb-3">
                            <label for="contactNumber" class="form-label">Contact Number *</label>
                            <input type="tel" class="form-control" id="contactNumber" name="contactNumber" required
                                   value="${contactNumber}" placeholder="Your contact number">
                        </div>
                        
                        <div class="d-grid gap-2 mt-4">
                            <button type="submit" class="btn btn-primary">Create Account</button>
                        </div>
                    </form>
                    
                    <div class="mt-3 text-center">
                        <p>Already have an account? <a href="${pageContext.request.contextPath}/login">Log In</a></p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Add custom validation script -->
<script>
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('registerForm');
    
    form.addEventListener('submit', function(event) {
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        
        // Check password length
        if (password.length < 8) {
            event.preventDefault();
            alert('Password must be at least 8 characters long');
            return false;
        }
        
        // Check if passwords match
        if (password !== confirmPassword) {
            event.preventDefault();
            alert('Passwords do not match');
            return false;
        }
    });
});
</script>

<jsp:include page="../common/footer.jsp" />