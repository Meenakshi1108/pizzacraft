<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="My Profile" />
</jsp:include>

<div class="container py-4">
    <h2 class="mb-4">
        <i class="fas fa-user-circle me-2"></i> My Profile
    </h2>

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

    <div class="row">
        <div class="col-lg-4 col-md-5 mb-4">
            <div class="card shadow-sm h-100">
                <div class="card-header bg-dark text-white">
                    <h5 class="mb-0">
                        <i class="fas fa-id-card me-2"></i> Account Info
                    </h5>
                </div>
                <div class="card-body text-center">
                    <div class="mb-4">
                        <i class="fas fa-user-circle fa-5x text-primary"></i>
                    </div>
                    <h5 class="card-title">${sessionScope.user.fullName}</h5>
                    <p class="card-text text-muted mb-1">@${sessionScope.user.username}</p>
                    <p class="card-text text-muted mb-1">
                        <i class="fas fa-envelope me-1"></i> ${sessionScope.user.email}
                    </p>
                    <p class="card-text text-muted">
                        <i class="fas fa-phone me-1"></i> ${sessionScope.user.contactNumber}
                    </p>
                    <div class="mt-3">
                        <span class="badge bg-primary">${sessionScope.user.role}</span>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-lg-8 col-md-7">
            <div class="card shadow-sm mb-4">
                <div class="card-header bg-white">
                    <h5 class="mb-0">
                        <i class="fas fa-user-edit me-2"></i> Edit Profile
                    </h5>
                </div>
                <div class="card-body">
                    <form id="profileForm" method="post" action="${pageContext.request.contextPath}/profile/update">
                        <div class="mb-3">
                            <label for="fullName" class="form-label">Full Name</label>
                            <input type="text" class="form-control" id="fullName" name="fullName" 
                                   value="${sessionScope.user.fullName}" required>
                        </div>
                        
                        <div class="mb-3">
                            <label for="email" class="form-label">Email</label>
                            <input type="email" class="form-control" id="email" name="email" 
                                   value="${sessionScope.user.email}" required>
                        </div>
                        
                        <div class="mb-3">
                            <label for="contactNumber" class="form-label">Contact Number</label>
                            <input type="tel" class="form-control" id="contactNumber" name="contactNumber" 
                                   value="${sessionScope.user.contactNumber}" required>
                            <div class="form-text">Please enter a valid contact number.</div>
                        </div>
                        
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save me-2"></i> Update Profile
                        </button>
                    </form>
                </div>
            </div>
            
            <div class="card shadow-sm">
                <div class="card-header bg-white">
                    <h5 class="mb-0">
                        <i class="fas fa-key me-2"></i> Change Password
                    </h5>
                </div>
                <div class="card-body">
                    <form id="passwordForm" method="post" action="${pageContext.request.contextPath}/profile/change-password">
                        <div class="mb-3">
                            <label for="currentPassword" class="form-label">Current Password</label>
                            <input type="password" class="form-control" id="currentPassword" name="currentPassword" required>
                        </div>
                        
                        <div class="mb-3">
                            <label for="newPassword" class="form-label">New Password</label>
                            <input type="password" class="form-control" id="newPassword" name="newPassword" 
                                   minlength="8" required>
                            <div class="form-text">Password must be at least 8 characters long.</div>
                        </div>
                        
                        <div class="mb-3">
                            <label for="confirmPassword" class="form-label">Confirm New Password</label>
                            <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" 
                                   minlength="8" required>
                        </div>
                        
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-lock me-2"></i> Change Password
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Add validation script -->
<script>
document.addEventListener('DOMContentLoaded', function() {
    // Password form validation
    const passwordForm = document.getElementById('passwordForm');
    passwordForm.addEventListener('submit', function(event) {
        const newPassword = document.getElementById('newPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        
        if (newPassword !== confirmPassword) {
            event.preventDefault();
            alert('The passwords do not match!');
        }
    });
    
    // Profile form validation
    const profileForm = document.getElementById('profileForm');
    profileForm.addEventListener('submit', function(event) {
        const contactNumber = document.getElementById('contactNumber').value;
        
        // Clean the number for validation
        let cleanNumber = contactNumber.replace(/[\s-]/g, '');
        
        // Handle +91 prefix
        if (cleanNumber.startsWith('+91')) {
            cleanNumber = cleanNumber.substring(3);
        }
        
        // Validate Indian mobile format
        const validFormat = /^[6-9]\d{9}$/.test(cleanNumber);
        
        if (!validFormat) {
            event.preventDefault();
            alert('Please enter a valid Indian mobile number.');
        }
    });
});
</script>

<jsp:include page="../common/footer.jsp" />